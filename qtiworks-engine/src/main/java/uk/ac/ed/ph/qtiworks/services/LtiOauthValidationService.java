/* Copyright (c) 2012-2013, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiNonce;
import uk.ac.ed.ph.qtiworks.services.dao.LtiNonceDao;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthServiceProvider;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.signature.OAuthSignatureMethod;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for validating LTI OAuth requests.
 * <p>
 * This reuses part of {@link SimpleOAuthValidator}, but uses the domain model to store and
 * check nonces. (Some bits of code from {@link SimpleOAuthValidator} have been pasted into here
 * and modified, as it's not easy to partially delegate to that class.)
 * <p>
 * This is NO authorisation at this level.
 *
 * @see SimpleOAuthValidator
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class LtiOauthValidationService {

    @Resource
    private LtiNonceDao ltiNonceDao;

    /**
     * Names of parameters that may not appear twice in a valid message.
     * This limitation is specified by OAuth Core
     * <a href="http://oauth.net/core/1.0#anchor7">section 5</a>.
     */
    private static final Set<String> SINGLE_PARAMETERS = new HashSet<String>(Arrays.asList(new String[] {
            OAuth.OAUTH_CONSUMER_KEY, OAuth.OAUTH_TOKEN, OAuth.OAUTH_TOKEN_SECRET,
            OAuth.OAUTH_CALLBACK, OAuth.OAUTH_SIGNATURE_METHOD, OAuth.OAUTH_SIGNATURE,
            OAuth.OAUTH_TIMESTAMP, OAuth.OAUTH_NONCE, OAuth.OAUTH_VERSION
        }
    ));

    /**
     * Validates the provided OAuth message against the given consumerKey and consumerSecret
     * and checks the timestamp and nonce
     * <p>
     * The method will complete successfully if the message is valid. If the message is invalid,
     * one of {@link IOException}, {@link OAuthException} or {@link URISyntaxException}
     * will be propagated up from {@link OAuthValidator}.
     *
     * @param oauthMessage
     * @param consumerKey
     * @param consumerSecret
     * @throws IOException
     * @throws OAuthException
     * @throws URISyntaxException
     */
    public void validateOAuthMessage(final OAuthMessage oauthMessage, final String consumerKey, final String consumerSecret)
            throws IOException, OAuthException, URISyntaxException {
        Assert.notNull(oauthMessage, "oauthMessage");
        Assert.notNull(consumerKey, "consumerKey");
        Assert.notNull(consumerSecret, "consumerSecret");
        final OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);
        final OAuthConsumer oauthConsumer = new OAuthConsumer(null, consumerKey, consumerSecret, serviceProvider);
        final OAuthAccessor oauthAccessor = new OAuthAccessor(oauthConsumer);

        final long messageTimestampSecs = Long.parseLong(oauthMessage.getParameter(OAuth.OAUTH_TIMESTAMP));
        final long currentTimestampMillis = System.currentTimeMillis();

        checkSingleParameters(oauthMessage);
        validateVersion(oauthMessage, 1.0, 1.0);
        validateSignature(oauthMessage, oauthAccessor);
        validateTimestamp(messageTimestampSecs, currentTimestampMillis, DomainConstants.OAUTH_TIMESTAMP_MAX_AGE);
        validateNonce(oauthMessage, messageTimestampSecs);
    }

    private void validateNonce(final OAuthMessage message, final long messageTimestampSecs) throws IOException, OAuthProblemException {
        /* Make sure this (nonce, consumer_key) pair hasn't already been recorded */
        final String nonce = message.getParameter(OAuth.OAUTH_NONCE);
        final String consumerKey = message.getConsumerKey();
        final LtiNonce existingNonce = ltiNonceDao.findByNonceAndConsumerKey(nonce, consumerKey);
        if (existingNonce!=null) {
            throw new OAuthProblemException(OAuth.Problems.NONCE_USED);
        }

        /* Record new nonce */
        final LtiNonce ltiNonce = new LtiNonce();
        ltiNonce.setNonce(nonce);
        ltiNonce.setConsumerKey(consumerKey);
        ltiNonce.setMessageTimestamp(new Date(1000L * messageTimestampSecs));
        ltiNonceDao.persist(ltiNonce);
    }

    /**
     * @see the <code>validateVersion()</code> method in {@link SimpleOAuthValidator}
     */
    private void validateVersion(final OAuthMessage message, final double minVersion, final double maxVersion)
            throws OAuthException, IOException {
        final String versionString = message.getParameter(OAuth.OAUTH_VERSION);
        if (versionString != null) {
            final double version = Double.parseDouble(versionString);
            if (version < minVersion || maxVersion < version) {
                final OAuthProblemException problem = new OAuthProblemException(OAuth.Problems.VERSION_REJECTED);
                problem.setParameter(OAuth.Problems.OAUTH_ACCEPTABLE_VERSIONS, minVersion + "-" + maxVersion);
                throw problem;
            }
        }
    }

    /**
     * @see the <code>validateSignature()</code> method in {@link SimpleOAuthValidator}
     */
    private void validateSignature(final OAuthMessage message, final OAuthAccessor accessor)
            throws OAuthException, IOException, URISyntaxException {
        message.requireParameters(OAuth.OAUTH_CONSUMER_KEY,
                OAuth.OAUTH_SIGNATURE_METHOD, OAuth.OAUTH_SIGNATURE);
        OAuthSignatureMethod.newSigner(message, accessor).validate(message);
    }

    /**
     * @see the <code>validateTimestamp()</code> method in {@link SimpleOAuthValidator}
     */
    private void validateTimestamp(final long messageTimestampSecs, final long currentTimestampMillis,
            final long maxAge)
            throws OAuthProblemException {
        final long minSecs = (currentTimestampMillis - maxAge + 500) / 1000L;
        final long maxSecs = (currentTimestampMillis + maxAge + 500) / 1000L;
        if (messageTimestampSecs < minSecs || maxSecs < messageTimestampSecs) {
            final OAuthProblemException problem = new OAuthProblemException(OAuth.Problems.TIMESTAMP_REFUSED);
            problem.setParameter(OAuth.Problems.OAUTH_ACCEPTABLE_TIMESTAMPS, minSecs + "-" + maxSecs);
            throw problem;
        }
    }

    /**
     * @see the <code>checkSingleParameters()</code> method in {@link SimpleOAuthValidator}
     */
    private void checkSingleParameters(final OAuthMessage message) throws IOException, OAuthException {
        // Check for repeated oauth_ parameters:
        boolean repeated = false;
        final Map<String, Collection<String>> nameToValues = new HashMap<String, Collection<String>>();
        for (final Map.Entry<String, String> parameter : message.getParameters()) {
            final String name = parameter.getKey();
            if (SINGLE_PARAMETERS.contains(name)) {
                Collection<String> values = nameToValues.get(name);
                if (values == null) {
                    values = new ArrayList<String>();
                    nameToValues.put(name, values);
                } else {
                    repeated = true;
                }
                values.add(parameter.getValue());
            }
        }
        if (repeated) {
            final Collection<OAuth.Parameter> rejected = new ArrayList<OAuth.Parameter>();
            for (final Map.Entry<String, Collection<String>> p : nameToValues.entrySet()) {
                final String name = p.getKey();
                final Collection<String> values = p.getValue();
                if (values.size() > 1) {
                    for (final String value : values) {
                        rejected.add(new OAuth.Parameter(name, value));
                    }
                }
            }
            final OAuthProblemException problem = new OAuthProblemException(OAuth.Problems.PARAMETER_REJECTED);
            problem.setParameter(OAuth.Problems.OAUTH_PARAMETERS_REJECTED, OAuth.formEncode(rejected));
            throw problem;
        }
    }

}
