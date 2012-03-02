package org.imsglobal.basiclti.provider.api;

/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2012 IMS GLobal Learning Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. 
 *
 **********************************************************************************/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * POJO representation of the BasicLTI attributes that are send with a Basic LTI request. 
 * Please check the 
 * <a href="http://www.imsglobal.org/lti/blti/bltiv1p0/ltiBLTIimgv1p0.html">IMS GLC Learning Tools Interoperability Basic LTI Implementation Guide</a>
 * for details.
 * </p>
 * <p>
 * The groups of attributes are clustered in nested classes.
 * </p>
 * @author Roland Groen (roland@edia.nl)
 *
 */
public class LtiContext implements Serializable {

	public static class Context implements Serializable {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -8452546593020970470L;

		protected String id;

		protected String type;

		protected String label;

		protected String title;

		public String getId() {
			return id;
		}

		public String getLabel() {
			return label;
		}

		public String getTitle() {
			return title;
		}

		public String getType() {
			return type;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

	public static class LaunchPresentation implements Serializable {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -3668024974071001204L;

		protected String documentTarget;

		protected String width;

		protected String height;

		protected String returnUrl;

		protected String locale;

		public String getDocumentTarget() {
			return documentTarget;
		}

		public String getHeight() {
			return height;
		}

		public String getLocale() {
			return locale;
		}

		public String getReturnUrl() {
			return returnUrl;
		}

		public String getWidth() {
			return width;
		}

		public void setDocumentTarget(String documentTarget) {
			this.documentTarget = documentTarget;
		}

		public void setHeight(String height) {
			this.height = height;
		}

		public void setLocale(String locale) {
			this.locale = locale;
		}

		public void setReturnUrl(String returnUrl) {
			this.returnUrl = returnUrl;
		}

		public void setWidth(String width) {
			this.width = width;
		}
	}

	public static class LisCourseOffering implements Serializable {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 4035919734269312765L;

		protected String sourcedId;

		public String getSourcedId() {
			return sourcedId;
		}

		public void setSourcedId(String sourcedId) {
			this.sourcedId = sourcedId;
		}
	}

	public static class LisCourseSection implements Serializable {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 6172473160350172269L;

		protected String sourcedId;

		public String getSourcedId() {
			return sourcedId;
		}

		public void setSourcedId(String sourcedId) {
			this.sourcedId = sourcedId;
		}
	}

	public static class LisPerson implements Serializable {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -2118983025355323945L;

		protected String sourcedId;

		protected String nameFull;

		protected String nameGiven;

		protected String nameFamily;

		protected String contactEmailPrimary;

		public String getContactEmailPrimary() {
			return contactEmailPrimary;
		}

		public String getNameFamily() {
			return nameFamily;
		}

		public String getNameFull() {
			return nameFull;
		}

		public String getNameGiven() {
			return nameGiven;
		}

		public String getSourcedId() {
			return sourcedId;
		}

		public void setContactEmailPrimary(String contactEmailPrimary) {
			this.contactEmailPrimary = contactEmailPrimary;
		}

		public void setNameFamily(String nameFamily) {
			this.nameFamily = nameFamily;
		}

		public void setNameFull(String nameFull) {
			this.nameFull = nameFull;
		}

		public void setNameGiven(String nameGiven) {
			this.nameGiven = nameGiven;
		}

		public void setSourcedId(String sourcedId) {
			this.sourcedId = sourcedId;
		}
	}

	public static class LisResult implements Serializable {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -260563980507729588L;

		protected String sourcedId;

		public String getSourcedId() {
			return sourcedId;
		}

		public void setSourcedId(String sourcedId) {
			this.sourcedId = sourcedId;
		}
	}

	public static class Oauth implements Serializable {

		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -377481211676063303L;

		protected String consumerKey;

		protected String callback;

		protected String nonce;

		protected String signature;

		protected String signatureMethod;

		protected String version;

		protected Long timestamp;

		public String getConsumerKey() {
			return consumerKey;
		}

		public String getCallback() {
			return callback;
		}

		public String getNonce() {
			return nonce;
		}

		public String getSignature() {
			return signature;
		}

		public String getSignatureMethod() {
			return signatureMethod;
		}

		public Long getTimestamp() {
			return timestamp;
		}

		public String getVersion() {
			return version;
		}

		public void setConsumerKey(String consumerKey) {
			this.consumerKey = consumerKey;
		}

		public void setCallback(String oauthCallback) {
			this.callback = oauthCallback;
		}

		public void setNonce(String oauthNonce) {
			this.nonce = oauthNonce;
		}

		public void setSignature(String oauthSignature) {
			this.signature = oauthSignature;
		}

		public void setSignatureMethod(String oauthSignatureMethod) {
			this.signatureMethod = oauthSignatureMethod;
		}

		public void setTimestamp(Long oauthTimestamp) {
			this.timestamp = oauthTimestamp;
		}

		public void setVersion(String oauthVersion) {
			this.version = oauthVersion;
		}

	}

	public static class ResourceLink implements Serializable {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 6377561937359801581L;

		protected String id;

		protected String title;

		protected String description;

		public String getDescription() {
			return description;
		}

		public String getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setTitle(String title) {
			this.title = title;
		}

	}

	public static class ToolConsumerInstance implements Serializable {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -6366307972429541726L;

		protected String guid;

		protected String name;

		protected String description;

		protected String url;

		protected String contactEmail;

		public String getContactEmail() {
			return contactEmail;
		}

		public String getDescription() {
			return description;
		}

		public String getGuid() {
			return guid;
		}

		public String getName() {
			return name;
		}

		public String getUrl() {
			return url;
		}

		public void setContactEmail(String contactEmail) {
			this.contactEmail = contactEmail;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setGuid(String guid) {
			this.guid = guid;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}

	public static class User implements Serializable {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -5577191889622641118L;

		protected String id;

		protected String image;

		protected List<String> roles = new ArrayList<String>(1);

		public String getId() {
			return id;
		}

		public String getImage() {
			return image;
		}

		public List<String> getRoles() {
			return roles;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public void setRoles(List<String> roles) {
			this.roles = roles;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1002385762193097086L;

	protected Context context = new Context();

	protected User user = new User();

	protected ResourceLink resourceLink = new ResourceLink();

	protected LisPerson lisPerson = new LisPerson();

	protected LisCourseOffering lisCourseOffering = new LisCourseOffering();

	protected LisCourseSection lisCourseSection = new LisCourseSection();

	protected LisResult lisResult = new LisResult();

	protected Oauth oauth = new Oauth();

	protected LaunchPresentation launchPresentation = new LaunchPresentation();

	protected ToolConsumerInstance toolConsumerInstance = new ToolConsumerInstance();

	public Context getContext() {
		return context;
	}

	public LaunchPresentation getLaunchPresentation() {
		return launchPresentation;
	}

	public LisCourseOffering getLisCourseOffering() {
		return lisCourseOffering;
	}

	public LisCourseSection getLisCourseSection() {
		return lisCourseSection;
	}

	public LisPerson getLisPerson() {
		return lisPerson;
	}

	public LisResult getLisResult() {
		return lisResult;
	}

	public Oauth getOauth() {
		return oauth;
	}

	public ResourceLink getResourceLink() {
		return resourceLink;
	}

	public ToolConsumerInstance getToolConsumerInstance() {
		return toolConsumerInstance;
	}

	public User getUser() {
		return user;
	}

	public void setOauth(Oauth oauth) {
		this.oauth = oauth;
	}

}
