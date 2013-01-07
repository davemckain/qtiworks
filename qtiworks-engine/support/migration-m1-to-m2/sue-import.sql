--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

--
-- Data for Name: delivery_settings; Type: TABLE DATA; Schema: public; Owner: qtiworks
--

COPY delivery_settings (dsid, type, author_mode, creation_time, public, prompt, template_processing_limit, title, owner_uid) FROM stdin;
17	ASSESSMENT_TEST	t	2012-11-15 11:30:09.081	f	\N	0	Default test delivery settings	4
\.


--
-- Data for Name: assessments; Type: TABLE DATA; Schema: public; Owner: qtiworks
--

COPY assessments (aid, type, creation_time, public, name, package_import_version, title, lock_version, default_dsid, owner_uid, sample_category_id) FROM stdin;
2296	ASSESSMENT_TEST	2012-11-15 11:30:01.798	f	MockTest001.zip	1	Mock Test	0	\N	4	\N
2299	ASSESSMENT_TEST	2012-11-15 14:43:39.164	f	MockTest02.zip	1	Mock Test	0	\N	4	\N
2302	ASSESSMENT_TEST	2012-11-16 11:35:42.572	f	MockTest03-allowReview.zip	1	Mock Test 3	0	\N	4	\N
2303	ASSESSMENT_TEST	2012-11-16 12:28:00.679	f	MockTest03.zip	1	Mock Test 3	0	\N	4	\N
2314	ASSESSMENT_TEST	2012-11-16 15:54:39.736	f	assessment01-Arithmetic.zip	1	Test01 Arithmetic (no review)	1	\N	4	\N
2411	ASSESSMENT_ITEM	2012-11-22 20:17:19.707	f	hebrew-gender002.xml	2	Hebrew gender exercise	2	\N	4	\N
2315	ASSESSMENT_TEST	2012-11-16 16:02:24.539	f	test01-Arithmetic.zip	3	Test01 Arithmetic	2	\N	4	\N
2394	ASSESSMENT_ITEM	2012-11-22 00:06:07.767	f	Test05-0111767-Stats001-freqTabMean.zip	2	Find the mean from a frequency table	1	\N	4	\N
2395	ASSESSMENT_ITEM	2012-11-22 00:07:24.502	f	Test05-0111769-Stats001-freqTabPercent.zip	2	Find the percentage in a given range from a frequency table	1	\N	4	\N
2404	ASSESSMENT_ITEM	2012-11-22 15:58:40.753	f	Test05-0111546-Stats001-makeFreqTab.zip	4	Construct a frequency table	3	\N	4	\N
2413	ASSESSMENT_ITEM	2012-11-22 20:23:19.615	f	RainbowWithImage.zip	2	Rainbow	2	\N	4	\N
2370	ASSESSMENT_ITEM	2012-11-20 23:58:24.082	f	Test05-0111542-Stats001-stemLeaf01.xml	3	Interpret a stem and leaf graph	2	\N	4	\N
2454	ASSESSMENT_TEST	2013-01-03 12:59:32.433	f	Test05-StatsAndVectors.zip	1	Test 05: Statistics and Vectors	0	\N	4	\N
2419	ASSESSMENT_ITEM	2012-11-22 20:46:34.762	f	UQ-nobleGases-01.xml	3	Noble Gases	2	\N	4	\N
2422	ASSESSMENT_ITEM	2012-11-22 20:50:58.309	f	multi-input.xml	1	Legend	0	\N	4	\N
2424	ASSESSMENT_ITEM	2012-11-22 20:59:06.748	f	choiceWithCertainty-polynomials.xml	1	Identifying polynomials	0	\N	4	\N
2456	ASSESSMENT_TEST	2013-01-03 14:15:14.947	f	Tut-05-Stats001-stemLeaf01.zip	1	Tutorial: Stem and leaf graphs	0	\N	4	\N
2436	ASSESSMENT_TEST	2012-11-24 17:39:35.505	f	Test03-Trig1.zip	1	Test03 Trigonometry 1	0	\N	4	\N
2441	ASSESSMENT_TEST	2012-11-25 20:46:03.757	f	Test04-Geometry1.zip	1	Test04 Geometry 1	0	\N	4	\N
2327	ASSESSMENT_TEST	2012-11-16 23:16:25.538	f	test02-Algebra1.zip	5	Test02 Algebra 1	4	\N	4	\N
2457	ASSESSMENT_TEST	2013-01-03 14:34:33.549	f	Tut-05-Stats001-freqTabMean.zip	2	Tutorial: Find the mean from a frequency table	1	\N	4	\N
2458	ASSESSMENT_TEST	2013-01-03 14:36:14.195	f	Tut-05-Stats001-freqTabPercent.zip	2	Tutorial: Find the percentage in a given range from a frequency table	1	\N	4	\N
2459	ASSESSMENT_TEST	2013-01-03 14:36:51.307	f	Tut-05-Stats001-makeFreqTab.zip	2	Tutorial: Construct a frequency table	1	\N	4	\N
2469	ASSESSMENT_TEST	2013-01-04 13:04:20.822	f	Test06-AlgAndTrig2.zip	2	Test 06: Algebra and Trigonometry 2	1	\N	4	\N
2486	ASSESSMENT_TEST	2013-01-04 18:27:36.114	f	Test07-Geometry2.zip	1	Test 07: Geometry 2	0	\N	4	\N
\.


--
-- Data for Name: assessment_packages; Type: TABLE DATA; Schema: public; Owner: qtiworks
--

COPY assessment_packages (apid, assessment_href, type, creation_time, import_type, import_version, sandbox_path, valid, validated, aid, importer_uid) FROM stdin;
2696	assessment.xml	ASSESSMENT_TEST	2012-11-15 11:30:01.798	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121115-113001798-25	t	t	2296	4
2699	assessment.xml	ASSESSMENT_TEST	2012-11-15 14:43:39.164	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121115-144339164-19	t	t	2299	4
2702	assessment.xml	ASSESSMENT_TEST	2012-11-16 11:35:42.572	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121116-113542572-44	t	t	2302	4
2703	assessment.xml	ASSESSMENT_TEST	2012-11-16 12:28:00.679	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121116-122800679-58	t	t	2303	4
2714	assessment.xml	ASSESSMENT_TEST	2012-11-16 15:54:39.736	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121116-155439736-33	t	t	2314	4
2715	assessment.xml	ASSESSMENT_TEST	2012-11-16 16:02:24.539	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121116-160224539-46	t	t	2315	4
2716	assessment.xml	ASSESSMENT_TEST	2012-11-16 17:55:59.602	CONTENT_PACKAGE	2	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121116-175559602-26	t	t	2315	4
2728	assessment.xml	ASSESSMENT_TEST	2012-11-16 23:16:25.538	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121116-231625538-41	t	t	2327	4
2736	assessment.xml	ASSESSMENT_TEST	2012-11-18 00:08:29.719	CONTENT_PACKAGE	3	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121118-000829719-60	t	t	2315	4
2737	assessment.xml	ASSESSMENT_TEST	2012-11-18 00:09:20.865	CONTENT_PACKAGE	2	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121118-000920865-61	t	t	2327	4
2845	assessment.xml	ASSESSMENT_TEST	2012-11-24 17:39:35.505	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121124-173935505-31	t	t	2436	4
2773	qti.xml	ASSESSMENT_ITEM	2012-11-20 23:58:24.082	STANDALONE_ITEM_XML	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121120-235824082-39	t	t	2370	4
2797	Test05-0111767-Stats001-freqTabMean.xml	ASSESSMENT_ITEM	2012-11-22 00:06:07.767	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121122-000607767-27	t	t	2394	4
2798	Test05-0111769-Stats001-freqTabPercent.xml	ASSESSMENT_ITEM	2012-11-22 00:07:24.502	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121122-000724502-83	t	t	2395	4
2807	Test05-0111546-Stats001-makeFreqTab.xml	ASSESSMENT_ITEM	2012-11-22 15:58:40.753	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121122-155840753-60	t	t	2404	4
2809	Test05-0111546-Stats001-makeFreqTab.xml	ASSESSMENT_ITEM	2012-11-22 16:07:35.135	CONTENT_PACKAGE	2	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121122-160735135-31	t	t	2404	4
2810	Test05-0111546-Stats001-makeFreqTab.xml	ASSESSMENT_ITEM	2012-11-22 16:15:43.181	CONTENT_PACKAGE	3	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121122-161543181-93	t	t	2404	4
2816	qti.xml	ASSESSMENT_ITEM	2012-11-22 20:17:19.707	STANDALONE_ITEM_XML	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121122-201719707-62	t	t	2411	4
2817	qti.xml	ASSESSMENT_ITEM	2012-11-22 20:19:32.756	STANDALONE_ITEM_XML	2	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121122-201932756-31	t	t	2411	4
2819	qti.xml	ASSESSMENT_ITEM	2012-11-22 20:23:19.615	STANDALONE_ITEM_XML	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121122-202319615-38	t	t	2413	4
2820	Rainbow.xml	ASSESSMENT_ITEM	2012-11-22 20:37:29.182	CONTENT_PACKAGE	2	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121122-203729182-14	t	t	2413	4
2826	qti.xml	ASSESSMENT_ITEM	2012-11-22 20:46:34.762	STANDALONE_ITEM_XML	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121122-204634762-60	t	t	2419	4
2829	qti.xml	ASSESSMENT_ITEM	2012-11-22 20:50:58.309	STANDALONE_ITEM_XML	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121122-205058309-83	t	t	2422	4
2831	qti.xml	ASSESSMENT_ITEM	2012-11-22 20:59:06.748	STANDALONE_ITEM_XML	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121122-205906748-70	t	t	2424	4
2832	qti.xml	ASSESSMENT_ITEM	2012-11-22 21:25:39.125	STANDALONE_ITEM_XML	2	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121122-212539125-74	t	t	2419	4
2833	qti.xml	ASSESSMENT_ITEM	2012-11-22 21:31:19.425	STANDALONE_ITEM_XML	3	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121122-213119425-89	t	t	2419	4
2870	qti.xml	ASSESSMENT_ITEM	2013-01-03 12:57:56.554	STANDALONE_ITEM_XML	3	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20130103-125756554-22	t	t	2370	4
2850	assessment.xml	ASSESSMENT_TEST	2012-11-25 20:46:03.757	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121125-204603757-60	t	t	2441	4
2871	assessment.xml	ASSESSMENT_TEST	2013-01-03 12:59:32.433	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20130103-125932433-45	t	t	2454	4
2873	assessment.xml	ASSESSMENT_TEST	2013-01-03 14:15:14.947	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20130103-141514947-54	t	t	2456	4
2856	qti.xml	ASSESSMENT_ITEM	2012-11-27 12:13:39.992	STANDALONE_ITEM_XML	2	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121127-121339992-29	t	t	2370	4
2874	assessment.xml	ASSESSMENT_TEST	2013-01-03 14:34:33.549	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20130103-143433549-43	f	t	2457	4
2857	Test05-0111767-Stats001-freqTabMean.xml	ASSESSMENT_ITEM	2012-11-27 12:14:14.937	CONTENT_PACKAGE	2	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121127-121414937-34	t	t	2394	4
2858	Test05-0111769-Stats001-freqTabPercent.xml	ASSESSMENT_ITEM	2012-11-27 12:14:48.834	CONTENT_PACKAGE	2	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121127-121448834-30	t	t	2395	4
2875	assessment.xml	ASSESSMENT_TEST	2013-01-03 14:36:14.195	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20130103-143614195-51	f	t	2458	4
2859	Test05-0111546-Stats001-makeFreqTab.xml	ASSESSMENT_ITEM	2012-11-27 12:15:32.179	CONTENT_PACKAGE	4	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121127-121532179-36	t	t	2404	4
2876	assessment.xml	ASSESSMENT_TEST	2013-01-03 14:36:51.307	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20130103-143651307-63	f	t	2459	4
2861	assessment.xml	ASSESSMENT_TEST	2012-12-03 15:36:02.442	CONTENT_PACKAGE	3	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121203-153602442-31	t	t	2327	4
2862	assessment.xml	ASSESSMENT_TEST	2012-12-03 16:22:03.257	CONTENT_PACKAGE	4	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121203-162203257-25	t	t	2327	4
2877	assessment.xml	ASSESSMENT_TEST	2013-01-03 14:50:15.291	CONTENT_PACKAGE	2	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20130103-145015291-52	t	t	2457	4
2863	assessment.xml	ASSESSMENT_TEST	2012-12-03 16:43:40.198	CONTENT_PACKAGE	5	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20121203-164340198-40	t	t	2327	4
2878	assessment.xml	ASSESSMENT_TEST	2013-01-03 14:53:12.964	CONTENT_PACKAGE	2	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20130103-145312964-40	t	t	2458	4
2879	assessment.xml	ASSESSMENT_TEST	2013-01-03 14:55:34	CONTENT_PACKAGE	2	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20130103-145534000-49	t	t	2459	4
2889	assessment.xml	ASSESSMENT_TEST	2013-01-04 13:04:20.822	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20130104-130420822-26	t	t	2469	4
2890	assessment.xml	ASSESSMENT_TEST	2013-01-04 13:59:36.63	CONTENT_PACKAGE	2	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20130104-135936630-63	t	t	2469	4
2907	assessment.xml	ASSESSMENT_TEST	2013-01-04 18:27:36.114	CONTENT_PACKAGE	1	/disk/webapps/QTIWorks-dev/data/assessments/instructor/suem/20130104-182736114-26	t	t	2486	4
\.


--
-- Data for Name: assessment_package_qti_files; Type: TABLE DATA; Schema: public; Owner: qtiworks
--

COPY assessment_package_qti_files (href, apid) FROM stdin;
Test05-0111546-Stats001-makeFreqTab.xml	2807
Test05-0111546-Stats001-makeFreqTab.xml	2809
uqsqtivpmelaxmlqjezfa/S-QTIv2p1-mela012062.xml	2736
assessment.xml	2736
uqstestaddfracxmlkncbcb/S-Test01-addFrac3.xml	2736
uqsqtivpmelaxmlhghrww/S-QTIv2p1-mela011684.xml	2736
uqsguratiomapsxmlyybypp/S-GU-Ratio-009-Maps.xml	2736
uqstestlcmxmlgwstqa/S-Test01-lcm-1.xml	2736
uqsqtivpmelaxmllbwykp/S-QTIv2p1-mela012019.xml	2736
uqsdiagnosysxmlnbvgvo/S-DIAGNOSYS302.xml	2736
uqsqtivpmelaxmlcjeoyo/S-QTIv2p1-mela011293.xml	2736
uqsqtivpmelaxmlexigxi/S-QTIv2p1-mela011027.xml	2736
uqsqtivpmelaxmlibirzy/S-QTIv2p1-mela011278.xml	2736
assessment.xml	2737
uqsmelalinineqxmlwsxhvz/S-mela011019-linIneq.xml	2737
uqsmelaquadformulaxmlmwnhey/S-mela011121-quadFormula.xml	2737
uqsmelasimpsqrtxmlykiaid/S-mela011592-simpSqrt.xml	2737
uqsmelalineqxmlirlndt/S-mela011034-lineq.xml	2737
uqsmelalineqdxmlfcfpyc/S-mela011034-lineq-d.xml	2737
uqsmelafactorisequadxmlbhdgpq/S-mela011119-factoriseQuad-2.xml	2737
uqsmelaspeedxmldntrfd/S-mela011650-Speed-003.xml	2737
uqsmelatranspositionxmlkvtvxv/S-mela012251-transposition09.xml	2737
uqsmelatranspositionxmldvfihn/S-mela012256-transposition14.xml	2737
uqsmelasubalgfracsxmlnkwkud/S-mela011663-subAlgFracs.xml	2737
uqsmelafactorisequadxmlcegife/S-mela011021-factoriseQuad.xml	2737
uqsmelasolvesimlineqsxmleeygpk/S-mela011016-solveSimLinEqs.xml	2737
uqsmelaevalbracketsxmlzdlwsl/S-mela011037-eval-brackets.xml	2737
uqsmelasimpindicesxmljomkze/S-mela011206-simpIndices.xml	2737
uqsmelacancelalgfracxmljjvmrs/S-mela011383-cancelAlgFrac.xml	2737
uqsmelaexpandquadxmlkgfzvd/S-mela011015-expandQuad.xml	2737
qti.xml	2773
Test05-0111767-Stats001-freqTabMean.xml	2797
Test05-0111769-Stats001-freqTabPercent.xml	2798
Test05-0111546-Stats001-makeFreqTab.xml	2810
qti.xml	2816
qti.xml	2817
qti.xml	2819
Rainbow.xml	2820
qti.xml	2856
Test05-0111767-Stats001-freqTabMean.xml	2857
Test05-0111769-Stats001-freqTabPercent.xml	2858
Test05-0111546-Stats001-makeFreqTab.xml	2859
assessment.xml	2861
uqsmelalinineqxmlwsxhvz/S-mela011019-linIneq.xml	2861
uqsmelaquadformulaxmlmwnhey/S-mela011121-quadFormula.xml	2861
uqsmelasimpsqrtxmlykiaid/S-mela011592-simpSqrt.xml	2861
uqsmelalineqxmlirlndt/S-mela011034-lineq.xml	2861
qti.xml	2826
qti.xml	2829
qti.xml	2831
qti.xml	2832
qti.xml	2833
assessment.xml	2845
uqmelatrirdangleobtusexmlrwrkkn/mela011417-tri3rdAngle-obtuse.xml	2845
uqmelarttrisidexmlhgixbg/mela011003-rtTriSide.xml	2845
uqmelatrigeqtanxmlupgpsn/mela011047-trigEq-tan0.xml	2845
uqcosinerulesideunitsxmlbqcitr/CosineRule-001-SideUnits.xml	2845
uqtesttriggraphsiziprgyncw/Test03-trigGraphs-011319i.xml	2845
uqtesttriggraphsizipxmincb/Test03-trigGraphs-011600i.xml	2845
uqtesttriggraphsmzipvevzve/Test03-trigGraphs-011316m.xml	2845
uqsinerulegetanglexmlykqznk/SineRule-001-getAngle.xml	2845
uqmelatrigeqsincosxmlzxxevd/mela011047-trigEq-sincos.xml	2845
uqmelarttrianglesincosxmlfexdpk/mela011003-rtTriAngle-sincos.xml	2845
uqsqtivpmelaxmlnxwmra/S-QTIv2p1-mela011027.xml	2696
assessment.xml	2696
uqsdiagnosysxmlxrwobd/S-DIAGNOSYS302.xml	2696
uqsguratiomapsxmlujqeyv/S-GU-Ratio-009-Maps.xml	2696
uqsqtivpmelaxmlnxwmra/S-QTIv2p1-mela011027.xml	2699
assessment.xml	2699
uqsdiagnosysxmlxrwobd/S-DIAGNOSYS302.xml	2699
uqsguratiomapsxmlujqeyv/S-GU-Ratio-009-Maps.xml	2699
assessment.xml	2702
uqsguratiomapsxmlihecfq/S-GU-Ratio-009-Maps.xml	2702
uqsdiagnosysxmlavayhf/S-DIAGNOSYS302.xml	2702
uqsqtivpmelaxmlssxmiy/S-QTIv2p1-mela011027.xml	2702
assessment.xml	2703
uqsguratiomapsxmlihecfq/S-GU-Ratio-009-Maps.xml	2703
uqsdiagnosysxmlavayhf/S-DIAGNOSYS302.xml	2703
uqsqtivpmelaxmlssxmiy/S-QTIv2p1-mela011027.xml	2703
uqsqtivpmelaxmlqjezfa/S-QTIv2p1-mela012062.xml	2714
assessment.xml	2714
uqstestaddfracxmlkncbcb/S-Test01-addFrac3.xml	2714
uqsqtivpmelaxmlhghrww/S-QTIv2p1-mela011684.xml	2714
uqsguratiomapsxmlyybypp/S-GU-Ratio-009-Maps.xml	2714
uqstestlcmxmlgwstqa/S-Test01-lcm-1.xml	2714
uqsqtivpmelaxmllbwykp/S-QTIv2p1-mela012019.xml	2714
uqsdiagnosysxmlnbvgvo/S-DIAGNOSYS302.xml	2714
uqsqtivpmelaxmlcjeoyo/S-QTIv2p1-mela011293.xml	2714
uqsqtivpmelaxmlexigxi/S-QTIv2p1-mela011027.xml	2714
uqsqtivpmelaxmlibirzy/S-QTIv2p1-mela011278.xml	2714
uqsqtivpmelaxmlqjezfa/S-QTIv2p1-mela012062.xml	2715
assessment.xml	2715
uqstestaddfracxmlkncbcb/S-Test01-addFrac3.xml	2715
uqsqtivpmelaxmlhghrww/S-QTIv2p1-mela011684.xml	2715
uqsguratiomapsxmlyybypp/S-GU-Ratio-009-Maps.xml	2715
uqstestlcmxmlgwstqa/S-Test01-lcm-1.xml	2715
uqsqtivpmelaxmllbwykp/S-QTIv2p1-mela012019.xml	2715
uqsdiagnosysxmlnbvgvo/S-DIAGNOSYS302.xml	2715
uqsqtivpmelaxmlcjeoyo/S-QTIv2p1-mela011293.xml	2715
uqsqtivpmelaxmlexigxi/S-QTIv2p1-mela011027.xml	2715
uqsqtivpmelaxmlibirzy/S-QTIv2p1-mela011278.xml	2715
uqsqtivpmelaxmlqjezfa/S-QTIv2p1-mela012062.xml	2716
assessment.xml	2716
uqstestaddfracxmlkncbcb/S-Test01-addFrac3.xml	2716
uqsqtivpmelaxmlhghrww/S-QTIv2p1-mela011684.xml	2716
uqsguratiomapsxmlyybypp/S-GU-Ratio-009-Maps.xml	2716
uqstestlcmxmlgwstqa/S-Test01-lcm-1.xml	2716
uqsqtivpmelaxmllbwykp/S-QTIv2p1-mela012019.xml	2716
uqsdiagnosysxmlnbvgvo/S-DIAGNOSYS302.xml	2716
uqsqtivpmelaxmlcjeoyo/S-QTIv2p1-mela011293.xml	2716
uqsqtivpmelaxmlexigxi/S-QTIv2p1-mela011027.xml	2716
uqsqtivpmelaxmlibirzy/S-QTIv2p1-mela011278.xml	2716
assessment.xml	2874
assessment.xml	2728
uqsmelalinineqxmlwsxhvz/S-mela011019-linIneq.xml	2728
uqsmelaquadformulaxmlmwnhey/S-mela011121-quadFormula.xml	2728
uqsmelasimpsqrtxmlykiaid/S-mela011592-simpSqrt.xml	2728
uqsmelalineqxmlirlndt/S-mela011034-lineq.xml	2728
uqsmelalineqdxmlfcfpyc/S-mela011034-lineq-d.xml	2728
uqsmelafactorisequadxmlbhdgpq/S-mela011119-factoriseQuad-2.xml	2728
uqsmelaspeedxmldntrfd/S-mela011650-Speed-003.xml	2728
uqsmelatranspositionxmlkvtvxv/S-mela012251-transposition09.xml	2728
uqsmelatranspositionxmldvfihn/S-mela012256-transposition14.xml	2728
uqsmelasubalgfracsxmlnkwkud/S-mela011663-subAlgFracs.xml	2728
uqsmelafactorisequadxmlcegife/S-mela011021-factoriseQuad.xml	2728
uqsmelasolvesimlineqsxmleeygpk/S-mela011016-solveSimLinEqs.xml	2728
uqsmelaevalbracketsxmlzdlwsl/S-mela011037-eval-brackets.xml	2728
uqsmelasimpindicesxmljomkze/S-mela011206-simpIndices.xml	2728
uqsmelacancelalgfracxmljjvmrs/S-mela011383-cancelAlgFrac.xml	2728
uqsmelaexpandquadxmlkgfzvd/S-mela011015-expandQuad.xml	2728
uqguformulaearclengthxmlobtlvh/GU-Formulae-ArcLength.xml	2850
assessment.xml	2850
uqteststrlinemxpcxmljmnokk/Test04-011377-strline001-mxpc.xml	2850
uqguformulaevolumespherexmlakqsbj/GuFormulae-VolumeSphere.xml	2850
uqtestsimfigslengthzipialmra/Test04-SimFigsLength-011610.xml	2850
uqtestradiussectorareadegxmlwvteyk/Test04-RadiusSectorArea-011023-deg.xml	2850
uqtestquadgraphsmzipxwcoki/Test04-quadGraphs-011017m.xml	2850
uqmelapythrampxmlshhata/mela011654-PythRamp.xml	2850
uqguformulaearcanglexmlsigpse/GU-Formulae-ArcAngle001.xml	2850
uqtestareabridgezipmwelkx/Test04-AreaBridge-011624.xml	2850
uqguformulaeareacirclexmltleonc/GU-Formulae-AreaCircle.xml	2850
uqsmelaevalbracketsxmlzdlwsl/S-mela011037-eval-brackets.xml	2861
uqsmelasimpindicesxmljomkze/S-mela011206-simpIndices.xml	2861
uqsmelacancelalgfracxmljjvmrs/S-mela011383-cancelAlgFrac.xml	2861
uqsmelaexpandquadxmlkgfzvd/S-mela011015-expandQuad.xml	2861
assessment.xml	2862
uqsmelalinineqxmlwsxhvz/S-mela011019-linIneq.xml	2862
uqsmelaquadformulaxmlmwnhey/S-mela011121-quadFormula.xml	2862
uqsmelasimpsqrtxmlykiaid/S-mela011592-simpSqrt.xml	2862
uqsmelalineqxmlirlndt/S-mela011034-lineq.xml	2862
uqsmelalineqdxmlfcfpyc/S-mela011034-lineq-d.xml	2862
uqsmelafactorisequadxmlbhdgpq/S-mela011119-factoriseQuad-2.xml	2862
uqsmelaspeedxmldntrfd/S-mela011650-Speed-003.xml	2862
uqsmelatranspositionxmlkvtvxv/S-mela012251-transposition09.xml	2862
uqsmelatranspositionxmldvfihn/S-mela012256-transposition14.xml	2862
uqsmelasubalgfracsxmlnkwkud/S-mela011663-subAlgFracs.xml	2862
uqsmelafactorisequadxmlcegife/S-mela011021-factoriseQuad.xml	2862
uqsmelasolvesimlineqsxmleeygpk/S-mela011016-solveSimLinEqs.xml	2862
uqsmelaevalbracketsxmlzdlwsl/S-mela011037-eval-brackets.xml	2862
uqsmelasimpindicesxmljomkze/S-mela011206-simpIndices.xml	2862
uqsmelacancelalgfracxmljjvmrs/S-mela011383-cancelAlgFrac.xml	2862
uqsmelaexpandquadxmlkgfzvd/S-mela011015-expandQuad.xml	2862
assessment.xml	2863
uqsmelalinineqxmlwsxhvz/S-mela011019-linIneq.xml	2863
uqsmelaquadformulaxmlmwnhey/S-mela011121-quadFormula.xml	2863
uqsmelasimpsqrtxmlykiaid/S-mela011592-simpSqrt.xml	2863
uqsmelalineqxmlirlndt/S-mela011034-lineq.xml	2863
uqsmelalineqdxmlfcfpyc/S-mela011034-lineq-d.xml	2863
uqsmelafactorisequadxmlbhdgpq/S-mela011119-factoriseQuad-2.xml	2863
uqsmelaspeedxmldntrfd/S-mela011650-Speed-003.xml	2863
uqsmelatranspositionxmlkvtvxv/S-mela012251-transposition09.xml	2863
uqsmelatranspositionxmldvfihn/S-mela012256-transposition14.xml	2863
uqsmelasubalgfracsxmlnkwkud/S-mela011663-subAlgFracs.xml	2863
uqsmelafactorisequadxmlcegife/S-mela011021-factoriseQuad.xml	2863
uqsmelasolvesimlineqsxmleeygpk/S-mela011016-solveSimLinEqs.xml	2863
uqsmelaevalbracketsxmlzdlwsl/S-mela011037-eval-brackets.xml	2863
uqsmelasimpindicesxmljomkze/S-mela011206-simpIndices.xml	2863
uqsmelacancelalgfracxmljjvmrs/S-mela011383-cancelAlgFrac.xml	2863
qti.xml	2870
uqteststatsfreqtabmeanzipqajsen/Test05-0111767-Stats001-freqTabMean.xml	2874
assessment.xml	2875
uqteststatsfreqtabpercentzipeywpbz/Test05-0111769-Stats001-freqTabPercent.xml	2875
assessment.xml	2876
uqteststatsmakefreqtabzipghtjxt/Test05-0111546-Stats001-makeFreqTab.xml	2876
assessment.xml	2877
uqteststatsfreqtabmeanzipimpgha/Test05-0111767-Stats001-freqTabMean.xml	2877
assessment.xml	2878
uqteststatsfreqtabpercentziphpkkpr/Test05-0111769-Stats001-freqTabPercent.xml	2878
assessment.xml	2879
uqteststatsmakefreqtabzipdsbbwf/Test05-0111546-Stats001-makeFreqTab.xml	2879
uqtestremdrthmxmlhzrafv/Test06-011132-remdrThm.xml	2889
assessment.xml	2889
uqtestfactrthmsyndivzipgcwknp/Test06-011129-factrThm-synDiv.xml	2889
uqtesttrigfuncsksincosxmpaxmlrmkwik/Test06-trigFuncs-ksincosxmpa.xml	2889
uqtestxsxmleqhjdz/Test06-423xs.xml	2889
uqtestquadineqxmlymsbow/Test06-204-Quad-Ineq.xml	2889
uqtestfactrthmxmlgcssjn/Test06-011130-factrThm.xml	2889
uqtestfuncoffuncxmlqmfcso/Test06-011418-funcOfFunc.xml	2889
uqtestquadgraphszipfqvopf/Test06-quadGraphs-011348.xml	2889
uqtestrttrisideradxmleuykim/Test06-011003-rtTriSide-rad.xml	2889
uqtestxmlpaknuj/Test06-425.xml	2889
uqtestremdrthmxmlhzrafv/Test06-011132-remdrThm.xml	2890
assessment.xml	2890
uqtestfactrthmsyndivzipgcwknp/Test06-011129-factrThm-synDiv.xml	2890
uqtesttrigfuncsksincosxmpaxmlrmkwik/Test06-trigFuncs-ksincosxmpa.xml	2890
uqtestxsxmleqhjdz/Test06-423xs.xml	2890
uqtestquadineqxmlymsbow/Test06-204-Quad-Ineq.xml	2890
uqtestfactrthmxmlgcssjn/Test06-011130-factrThm.xml	2890
uqtestfuncoffuncxmlqmfcso/Test06-011418-funcOfFunc.xml	2890
uqtestquadgraphszipfqvopf/Test06-quadGraphs-011348.xml	2890
uqtestrttrisideradxmleuykim/Test06-011003-rtTriSide-rad.xml	2890
uqtestxmlpaknuj/Test06-425.xml	2890
uqsmelalineqdxmlfcfpyc/S-mela011034-lineq-d.xml	2861
uqsmelafactorisequadxmlbhdgpq/S-mela011119-factoriseQuad-2.xml	2861
uqsmelaspeedxmldntrfd/S-mela011650-Speed-003.xml	2861
uqsmelatranspositionxmlkvtvxv/S-mela012251-transposition09.xml	2861
uqsmelatranspositionxmldvfihn/S-mela012256-transposition14.xml	2861
uqsmelasubalgfracsxmlnkwkud/S-mela011663-subAlgFracs.xml	2861
uqsmelafactorisequadxmlcegife/S-mela011021-factoriseQuad.xml	2861
uqsmelasolvesimlineqsxmleeygpk/S-mela011016-solveSimLinEqs.xml	2861
uqsmelaexpandquadxmlkgfzvd/S-mela011015-expandQuad.xml	2863
uqteststatsfreqtabpercentzipynkkmq/Test05-0111769-Stats001-freqTabPercent.xml	2871
assessment.xml	2871
uqteststatsmeanxmlgvrjqh/Test05-0111631-Stats001-mean02.xml	2871
uqteststatsfreqtabmeanzipmswgmf/Test05-0111767-Stats001-freqTabMean.xml	2871
uqteststatsmeanxmlcwcjgd/Test05-0111548-Stats001-mean03.xml	2871
uqtestdvectorunitvectxmltacfxy/Test05-011078-2dVector001-unitVect.xml	2871
uqtestdvectormagnvectxmlhfhgkc/Test05-011339-2dVector002-magnVect.xml	2871
uqteststatsmakefreqtabzipvjngvs/Test05-0111546-Stats001-makeFreqTab.xml	2871
uqteststatsstemleafxmlssnvli/Test05-0111542-Stats001-stemLeaf01.xml	2871
uqteststatsmodexmlayqifd/Test05-0111550-Stats001-mode01.xml	2871
uqteststrlinelineptslopexmlyqqyvf/Test07-011126-strline003-linePtSlope.xml	2907
uqguformulaearclengthradianformxmlqynfog/GU-Formulae-ArcLength-radianform.xml	2907
assessment.xml	2907
uqteststrlinelineparaperpxmlyodjqr/Test07-011024-strline003-lineParaPerp.xml	2907
uqteststrlinegdtfromanglezipvlurbf/Test07-011458-strline005-gdtFromAngle.xml	2907
uqteststrlinemidptxmlekaaru/Test07-011378-strline004-MidPt.xml	2907
uqteststrlinelinemidptlengthxmlpqneeo/Test07-011031-strline004-lineMidPtLength.xml	2907
uqtestradtodegtoradxmlmjfdns/Test07-011358-radToDegToRad.xml	2907
uqarcsectordeguseradacutexmljhgnhr/ArcSector002-degUseRad-acute.xml	2907
uqteststrlinegdtperpxmligwjft/Test07-011576-strline002-gdtPerp.xml	2907
uqteststrlinelineptsxmllncoaj/Test07-011127-strline003-line2Pts.xml	2907
uqteststatssdzipdxapfe/Test05-0111547-Stats001-sd01.xml	2871
assessment.xml	2873
uqteststatsstemleafxmlsxvcte/Test05-0111542-Stats001-stemLeaf01.xml	2873
\.


--
-- Data for Name: assessment_package_safe_files; Type: TABLE DATA; Schema: public; Owner: qtiworks
--

COPY assessment_package_safe_files (href, apid) FROM stdin;
imsmanifest.xml	2696
imsmanifest.xml	2699
imsmanifest.xml	2702
imsmanifest.xml	2703
uqtesttriggraphsmzipvevzve/images/-2cos(6x).png	2845
uqtesttriggraphsmzipvevzve/images/4sin(6x).png	2845
uqtesttriggraphsiziprgyncw/images/4cos(6x).png	2845
uqtesttriggraphsmzipvevzve/images/cos(5x).png	2845
uqtesttriggraphsiziprgyncw/images/cos(3x).png	2845
uqtesttriggraphsmzipvevzve/images/2sin(2x).png	2845
uqtesttriggraphsiziprgyncw/images/4cos(2x).png	2845
uqtesttriggraphsiziprgyncw/images/2sin(5x).png	2845
uqtesttriggraphsiziprgyncw/images/-4cos(5x).png	2845
uqtesttriggraphsmzipvevzve/images/2cos(x).png	2845
uqtesttriggraphsmzipvevzve/images/3sin(4x).png	2845
uqtesttriggraphsmzipvevzve/images/-2cos(3x).png	2845
uqtesttriggraphsiziprgyncw/images/3cos(3x).png	2845
uqtestquadgraphsmzipxwcoki/images/quad-2.png	2850
uqtestareabridgezipmwelkx/images/stoneBridge.png	2850
uqtestquadgraphsmzipxwcoki/images/quad-m2.png	2850
imsmanifest.xml	2850
uqtestsimfigslengthzipialmra/images/simTriangles.png	2850
uqtestquadgraphsmzipxwcoki/imsmanifest.xml	2850
uqtestquadgraphsmzipxwcoki/images/quad-0p5.png	2850
uqtestquadgraphsmzipxwcoki/images/quad-m1.png	2850
uqtestquadgraphsmzipxwcoki/images/quad-m0p5.png	2850
imsmanifest.xml	2857
images/styles.css	2857
imsmanifest.xml	2714
imsmanifest.xml	2715
imsmanifest.xml	2716
imsmanifest.xml	2728
imsmanifest.xml	2736
imsmanifest.xml	2737
imsmanifest.xml	2797
images/styles.css	2797
imsmanifest.xml	2798
images/styles.css	2798
imsmanifest.xml	2807
images/styles.css	2807
imsmanifest.xml	2809
images/styles.css	2809
imsmanifest.xml	2810
images/styles.css	2810
rainbow.jpg	2820
imsmanifest.xml	2858
uqtesttriggraphsiziprgyncw/images/-2cos(3x).png	2845
uqtesttriggraphsmzipvevzve/images/2sin(6x).png	2845
uqtesttriggraphsmzipvevzve/images/-3cos(4x).png	2845
uqtesttriggraphsiziprgyncw/images/-2sin(x).png	2845
uqtesttriggraphsiziprgyncw/images/-3cos(5x).png	2845
uqtesttriggraphsmzipvevzve/images/5cos(5x).png	2845
uqtesttriggraphsmzipvevzve/images/3sin(5x).png	2845
uqtesttriggraphsiziprgyncw/images/5sin(2x).png	2845
uqtesttriggraphsiziprgyncw/images/-3cos(6x).png	2845
uqtesttriggraphsiziprgyncw/images/-sin(3x).png	2845
uqtesttriggraphsmzipvevzve/images/2cos(5x).png	2845
uqtesttriggraphsizipxmincb/images/sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/-2sin(3x).png	2845
uqtesttriggraphsmzipvevzve/images/-2cos(x).png	2845
uqtesttriggraphsmzipvevzve/images/4sin(2x).png	2845
uqtesttriggraphsiziprgyncw/images/-4cos(2x).png	2845
uqtesttriggraphsmzipvevzve/images/2cos(6x).png	2845
uqtesttriggraphsiziprgyncw/images/-3sin(2x).png	2845
uqtesttriggraphsmzipvevzve/images/-5sin(2x).png	2845
uqtesttriggraphsmzipvevzve/images/-3sin(5x).png	2845
uqtesttriggraphsiziprgyncw/images/3sin(6x).png	2845
uqtesttriggraphsiziprgyncw/images/-5sin(4x).png	2845
uqtesttriggraphsmzipvevzve/images/3cos(5x).png	2845
uqtesttriggraphsiziprgyncw/images/-3cos(3x).png	2845
uqtesttriggraphsiziprgyncw/images/-5cos(6x).png	2845
uqtesttriggraphsmzipvevzve/images/-sin(2x).png	2845
uqtesttriggraphsmzipvevzve/images/2sin(5x).png	2845
uqtesttriggraphsmzipvevzve/images/-2sin(2x).png	2845
uqtesttriggraphsiziprgyncw/images/2cos(4x).png	2845
uqtesttriggraphsizipxmincb/images/sin(3x).png	2845
uqtesttriggraphsiziprgyncw/images/2cos(3x).png	2845
uqtesttriggraphsiziprgyncw/images/3sin(5x).png	2845
uqtesttriggraphsiziprgyncw/images/cos(x).png	2845
uqtesttriggraphsmzipvevzve/images/sin(3x).png	2845
uqtesttriggraphsmzipvevzve/images/-sin(5x).png	2845
uqtesttriggraphsiziprgyncw/images/3sin(3x).png	2845
uqtesttriggraphsmzipvevzve/images/5cos(x).png	2845
uqtesttriggraphsmzipvevzve/images/5sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/-4sin(4x).png	2845
uqtesttriggraphsiziprgyncw/images/-sin(2x).png	2845
uqtesttriggraphsiziprgyncw/images/-3sin(6x).png	2845
uqtesttriggraphsmzipvevzve/images/-cos(4x).png	2845
uqtesttriggraphsmzipvevzve/images/-sin(3x).png	2845
uqtesttriggraphsiziprgyncw/images/-2sin(4x).png	2845
imsmanifest.xml	2845
uqtesttriggraphsmzipvevzve/images/-2sin(4x).png	2845
uqtesttriggraphsmzipvevzve/images/3sin(3x).png	2845
uqtesttriggraphsmzipvevzve/images/-4cos(6x).png	2845
uqtesttriggraphsmzipvevzve/images/4cos(6x).png	2845
uqtesttriggraphsiziprgyncw/images/-3cos(x).png	2845
uqtesttriggraphsmzipvevzve/images/cos(6x).png	2845
uqtesttriggraphsmzipvevzve/images/-5sin(3x).png	2845
uqteststatssdzipdxapfe/imsmanifest.xml	2871
uqteststatsfreqtabpercentzipynkkmq/imsmanifest.xml	2871
uqteststatsmakefreqtabzipvjngvs/images/styles.css	2871
uqteststatssdzipdxapfe/images/styles.css	2871
uqteststatsfreqtabpercentzipynkkmq/images/styles.css	2871
imsmanifest.xml	2871
uqteststatsfreqtabmeanzipmswgmf/images/styles.css	2871
uqteststatsmakefreqtabzipvjngvs/imsmanifest.xml	2871
uqteststatsfreqtabmeanzipmswgmf/imsmanifest.xml	2871
imsmanifest.xml	2873
uqteststatsfreqtabmeanzipqajsen/imsmanifest.xml	2874
imsmanifest.xml	2874
uqteststatsfreqtabmeanzipqajsen/images/styles.css	2874
uqteststatsfreqtabpercentzipeywpbz/imsmanifest.xml	2875
imsmanifest.xml	2875
uqteststatsfreqtabpercentzipeywpbz/images/styles.css	2875
uqteststatsmakefreqtabzipghtjxt/images/styles.css	2876
imsmanifest.xml	2876
uqteststatsmakefreqtabzipghtjxt/imsmanifest.xml	2876
uqteststatsfreqtabmeanzipimpgha/imsmanifest.xml	2877
uqteststatsfreqtabmeanzipimpgha/images/styles.css	2877
imsmanifest.xml	2877
imsmanifest.xml	2878
uqteststatsfreqtabpercentziphpkkpr/images/styles.css	2878
images/styles.css	2858
imsmanifest.xml	2859
images/styles.css	2859
imsmanifest.xml	2861
imsmanifest.xml	2862
imsmanifest.xml	2863
uqteststatsfreqtabpercentziphpkkpr/imsmanifest.xml	2878
imsmanifest.xml	2879
uqteststatsmakefreqtabzipdsbbwf/images/styles.css	2879
uqteststatsmakefreqtabzipdsbbwf/imsmanifest.xml	2879
imsmanifest.xml	2907
uqtesttriggraphsiziprgyncw/images/-4cos(6x).png	2845
uqtesttriggraphsmzipvevzve/images/-3sin(6x).png	2845
uqtesttriggraphsmzipvevzve/images/-4sin(2x).png	2845
uqtesttriggraphsiziprgyncw/images/4cos(5x).png	2845
uqtesttriggraphsmzipvevzve/images/4sin(3x).png	2845
uqtesttriggraphsmzipvevzve/images/sin(2x).png	2845
uqtesttriggraphsiziprgyncw/images/5sin(6x).png	2845
uqtesttriggraphsmzipvevzve/images/3cos(2x).png	2845
uqtesttriggraphsmzipvevzve/images/4cos(4x).png	2845
uqtesttriggraphsiziprgyncw/images/4sin(4x).png	2845
uqtesttriggraphsiziprgyncw/images/-cos(6x).png	2845
uqtesttriggraphsiziprgyncw/images/3cos(4x).png	2845
uqtesttriggraphsiziprgyncw/images/-sin(5x).png	2845
uqtesttriggraphsiziprgyncw/images/-2cos(6x).png	2845
uqtesttriggraphsizipxmincb/images/sin(2x).png	2845
uqtesttriggraphsmzipvevzve/images/5sin(4x).png	2845
uqtesttriggraphsmzipvevzve/images/-2sin(6x).png	2845
uqtesttriggraphsmzipvevzve/images/-3sin(3x).png	2845
uqtesttriggraphsiziprgyncw/images/-3sin(3x).png	2845
uqtesttriggraphsiziprgyncw/images/-cos(x).png	2845
uqtesttriggraphsmzipvevzve/images/-sin(4x).png	2845
uqtesttriggraphsmzipvevzve/images/5sin(5x).png	2845
uqtesttriggraphsmzipvevzve/images/5cos(4x).png	2845
uqtesttriggraphsizipxmincb/imsmanifest.xml	2845
uqtesttriggraphsiziprgyncw/images/2cos(6x).png	2845
uqtesttriggraphsmzipvevzve/images/-cos(x).png	2845
uqtesttriggraphsiziprgyncw/images/4cos(x).png	2845
uqtesttriggraphsiziprgyncw/images/-4cos(3x).png	2845
uqtesttriggraphsmzipvevzve/images/3sin(2x).png	2845
uqtesttriggraphsmzipvevzve/images/5sin(3x).png	2845
uqtesttriggraphsiziprgyncw/images/-cos(3x).png	2845
uqtesttriggraphsizipxmincb/images/sin(4x).png	2845
uqtesttriggraphsiziprgyncw/images/4sin(3x).png	2845
uqtesttriggraphsmzipvevzve/images/cos(4x).png	2845
uqtesttriggraphsiziprgyncw/images/5cos(5x).png	2845
uqtesttriggraphsiziprgyncw/images/cos(4x).png	2845
uqtesttriggraphsiziprgyncw/images/-2sin(2x).png	2845
uqtesttriggraphsiziprgyncw/images/3sin(4x).png	2845
uqtesttriggraphsiziprgyncw/images/-3sin(5x).png	2845
uqtesttriggraphsiziprgyncw/images/5sin(3x).png	2845
uqtesttriggraphsizipxmincb/images/sin(5x).png	2845
uqtesttriggraphsmzipvevzve/images/4sin(5x).png	2845
uqtesttriggraphsmzipvevzve/images/2sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/-3cos(6x).png	2845
uqtesttriggraphsmzipvevzve/images/-4cos(5x).png	2845
uqtesttriggraphsmzipvevzve/images/-5cos(5x).png	2845
uqtesttriggraphsiziprgyncw/images/cos(6x).png	2845
uqtesttriggraphsiziprgyncw/images/sin(4x).png	2845
uqtesttriggraphsiziprgyncw/images/sin(5x).png	2845
uqtesttriggraphsmzipvevzve/images/-2sin(5x).png	2845
uqtesttriggraphsiziprgyncw/images/-5sin(5x).png	2845
uqtesttriggraphsiziprgyncw/images/4sin(6x).png	2845
uqtesttriggraphsiziprgyncw/images/-2sin(3x).png	2845
uqtesttriggraphsiziprgyncw/images/cos(2x).png	2845
uqtesttriggraphsmzipvevzve/images/-4sin(3x).png	2845
uqtesttriggraphsmzipvevzve/images/-4cos(3x).png	2845
uqtesttriggraphsmzipvevzve/images/-5cos(3x).png	2845
uqtesttriggraphsmzipvevzve/images/-3sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/-2sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/-2cos(4x).png	2845
uqtesttriggraphsmzipvevzve/images/-5sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/-sin(x).png	2845
uqtesttriggraphsiziprgyncw/images/-sin(6x).png	2845
uqtesttriggraphsmzipvevzve/images/2sin(4x).png	2845
uqtesttriggraphsiziprgyncw/images/5cos(3x).png	2845
uqtesttriggraphsiziprgyncw/images/4cos(3x).png	2845
uqtesttriggraphsmzipvevzve/images/-3sin(2x).png	2845
uqtesttriggraphsiziprgyncw/imsmanifest.xml	2845
uqtesttriggraphsmzipvevzve/images/-cos(3x).png	2845
uqtesttriggraphsmzipvevzve/images/3cos(6x).png	2845
uqtesttriggraphsmzipvevzve/images/-5cos(x).png	2845
uqtesttriggraphsmzipvevzve/images/-cos(2x).png	2845
uqtesttriggraphsiziprgyncw/images/-5cos(5x).png	2845
uqtesttriggraphsiziprgyncw/images/-5cos(x).png	2845
uqtesttriggraphsiziprgyncw/images/sin(3x).png	2845
uqtesttriggraphsmzipvevzve/images/sin(4x).png	2845
uqtesttriggraphsmzipvevzve/images/-4cos(2x).png	2845
uqtesttriggraphsiziprgyncw/images/sin(6x).png	2845
uqtesttriggraphsiziprgyncw/images/-3cos(4x).png	2845
uqtesttriggraphsiziprgyncw/images/-2sin(6x).png	2845
uqtesttriggraphsiziprgyncw/images/3cos(6x).png	2845
uqtesttriggraphsiziprgyncw/images/-2cos(2x).png	2845
uqtesttriggraphsiziprgyncw/images/cos(5x).png	2845
uqtesttriggraphsiziprgyncw/images/2cos(2x).png	2845
uqtesttriggraphsmzipvevzve/images/4sin(4x).png	2845
uqtesttriggraphsmzipvevzve/images/-4sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/4cos(x).png	2845
uqtesttriggraphsiziprgyncw/images/-3sin(4x).png	2845
uqtesttriggraphsiziprgyncw/images/5cos(2x).png	2845
uqtesttriggraphsmzipvevzve/images/-5cos(6x).png	2845
uqtesttriggraphsiziprgyncw/images/-sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/-sin(6x).png	2845
uqtesttriggraphsmzipvevzve/images/-4cos(x).png	2845
uqtesttriggraphsmzipvevzve/images/-3cos(2x).png	2845
uqtesttriggraphsiziprgyncw/images/5sin(4x).png	2845
uqtesttriggraphsmzipvevzve/images/2cos(4x).png	2845
uqtesttriggraphsiziprgyncw/images/-4cos(x).png	2845
uqtesttriggraphsiziprgyncw/images/-5sin(6x).png	2845
uqtesttriggraphsiziprgyncw/images/5cos(x).png	2845
uqtesttriggraphsiziprgyncw/images/-5sin(x).png	2845
uqtesttriggraphsiziprgyncw/images/5sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/cos(3x).png	2845
uqtesttriggraphsmzipvevzve/images/-5cos(2x).png	2845
uqtesttriggraphsiziprgyncw/images/-4sin(5x).png	2845
uqtesttriggraphsmzipvevzve/images/3cos(x).png	2845
uqtesttriggraphsiziprgyncw/images/-2sin(5x).png	2845
uqtesttriggraphsiziprgyncw/images/-4sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/-2cos(2x).png	2845
uqtesttriggraphsiziprgyncw/images/-cos(4x).png	2845
uqtesttriggraphsiziprgyncw/images/2sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/sin(5x).png	2845
uqtesttriggraphsiziprgyncw/images/sin(2x).png	2845
uqtesttriggraphsmzipvevzve/images/5sin(2x).png	2845
uqtesttriggraphsiziprgyncw/images/2cos(x).png	2845
uqtesttriggraphsiziprgyncw/images/-3cos(2x).png	2845
uqtesttriggraphsiziprgyncw/images/-cos(2x).png	2845
uqtesttriggraphsiziprgyncw/images/3cos(x).png	2845
uqtesttriggraphsmzipvevzve/images/4sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/-2cos(5x).png	2845
uqtesttriggraphsmzipvevzve/images/-5cos(4x).png	2845
uqtesttriggraphsiziprgyncw/images/-4cos(4x).png	2845
uqtesttriggraphsmzipvevzve/images/-5sin(4x).png	2845
uqtesttriggraphsmzipvevzve/images/5sin(6x).png	2845
uqtesttriggraphsiziprgyncw/images/3sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/-4sin(6x).png	2845
uqtesttriggraphsmzipvevzve/images/-4cos(4x).png	2845
uqtesttriggraphsmzipvevzve/images/-cos(5x).png	2845
uqtesttriggraphsmzipvevzve/images/3cos(3x).png	2845
uqtesttriggraphsmzipvevzve/images/4cos(3x).png	2845
uqtesttriggraphsiziprgyncw/images/-4sin(6x).png	2845
uqtesttriggraphsiziprgyncw/images/-5sin(2x).png	2845
uqtesttriggraphsiziprgyncw/images/2sin(4x).png	2845
uqtesttriggraphsmzipvevzve/images/-3cos(3x).png	2845
uqtesttriggraphsiziprgyncw/images/-4sin(4x).png	2845
uqtesttriggraphsiziprgyncw/images/-5cos(4x).png	2845
uqtesttriggraphsmzipvevzve/images/4cos(2x).png	2845
uqtesttriggraphsiziprgyncw/images/-4sin(2x).png	2845
uqtesttriggraphsmzipvevzve/images/cos(2x).png	2845
uqtesttriggraphsiziprgyncw/images/-4sin(3x).png	2845
uqtesttriggraphsiziprgyncw/images/-5cos(2x).png	2845
uqtesttriggraphsiziprgyncw/images/3cos(2x).png	2845
uqtesttriggraphsiziprgyncw/images/5cos(4x).png	2845
uqtesttriggraphsmzipvevzve/images/-5sin(5x).png	2845
uqtesttriggraphsiziprgyncw/images/sin(x).png	2845
uqtesttriggraphsiziprgyncw/images/3sin(2x).png	2845
uqtesttriggraphsizipxmincb/images/sin(6x).png	2845
uqtesttriggraphsiziprgyncw/images/3cos(5x).png	2845
uqtesttriggraphsmzipvevzve/images/-cos(6x).png	2845
uqtesttriggraphsiziprgyncw/images/5sin(5x).png	2845
uqtesttriggraphsiziprgyncw/images/2cos(5x).png	2845
uqtesttriggraphsiziprgyncw/images/-5cos(3x).png	2845
uqtesttriggraphsmzipvevzve/images/-4sin(5x).png	2845
uqtesttriggraphsiziprgyncw/images/-3sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/3sin(6x).png	2845
uqtesttriggraphsiziprgyncw/images/2sin(6x).png	2845
uqtesttriggraphsiziprgyncw/images/-5sin(3x).png	2845
uqtesttriggraphsmzipvevzve/images/2cos(2x).png	2845
uqtesttriggraphsmzipvevzve/images/cos(x).png	2845
uqtesttriggraphsmzipvevzve/images/-3cos(x).png	2845
uqtesttriggraphsmzipvevzve/images/-3sin(4x).png	2845
uqtesttriggraphsiziprgyncw/images/-2cos(5x).png	2845
uqtesttriggraphsiziprgyncw/images/-cos(5x).png	2845
uqtesttriggraphsiziprgyncw/images/4cos(4x).png	2845
uqtesttriggraphsmzipvevzve/images/2cos(3x).png	2845
uqtesttriggraphsiziprgyncw/images/2sin(3x).png	2845
uqtesttriggraphsmzipvevzve/images/-5sin(6x).png	2845
uqtesttriggraphsiziprgyncw/images/4sin(5x).png	2845
uqtesttriggraphsmzipvevzve/imsmanifest.xml	2845
uqtesttriggraphsmzipvevzve/images/5cos(3x).png	2845
uqtesttriggraphsmzipvevzve/images/3sin(x).png	2845
uqtesttriggraphsmzipvevzve/images/sin(6x).png	2845
uqtesttriggraphsiziprgyncw/images/-2cos(x).png	2845
uqtesttriggraphsmzipvevzve/images/3cos(4x).png	2845
uqtesttriggraphsiziprgyncw/images/-2cos(4x).png	2845
uqtesttriggraphsmzipvevzve/images/5cos(6x).png	2845
uqtesttriggraphsmzipvevzve/images/4cos(5x).png	2845
uqtesttriggraphsmzipvevzve/images/5cos(2x).png	2845
uqtesttriggraphsmzipvevzve/images/-3cos(5x).png	2845
uqtesttriggraphsiziprgyncw/images/5cos(6x).png	2845
uqtesttriggraphsmzipvevzve/images/2sin(3x).png	2845
uqtesttriggraphsmzipvevzve/images/sin(x).png	2845
uqtesttriggraphsiziprgyncw/images/4sin(x).png	2845
uqtesttriggraphsiziprgyncw/images/4sin(2x).png	2845
uqtesttriggraphsiziprgyncw/images/-sin(4x).png	2845
uqtesttriggraphsiziprgyncw/images/2sin(2x).png	2845
uqtestfactrthmsyndivzipgcwknp/imsmanifest.xml	2889
uqtestfactrthmsyndivzipgcwknp/images/styles.css	2889
uqtestquadgraphszipfqvopf/images/0rootsPosGt0.png	2889
uqtestquadgraphszipfqvopf/images/1rootPosLt0.png	2889
uqtestquadgraphszipfqvopf/imsmanifest.xml	2889
uqtestquadgraphszipfqvopf/images/2rootsPosGt0.png	2889
uqtestquadgraphszipfqvopf/images/1rootPosGt0.png	2889
uqtestquadgraphszipfqvopf/images/2rootsNegGt0.png	2889
uqtestquadgraphszipfqvopf/images/0rootsNegLt0.png	2889
uqtestquadgraphszipfqvopf/images/0rootsNegGt0.png	2889
uqtestquadgraphszipfqvopf/images/1rootNegGt0.png	2889
uqtestquadgraphszipfqvopf/images/1rootNegLt0.png	2889
uqtestquadgraphszipfqvopf/images/0rootsPosLt0.png	2889
uqtestquadgraphszipfqvopf/images/2rootsPosLt0.png	2889
imsmanifest.xml	2889
uqtestquadgraphszipfqvopf/images/2rootsNegLt0.png	2889
uqtestfactrthmsyndivzipgcwknp/imsmanifest.xml	2890
uqtestfactrthmsyndivzipgcwknp/images/styles.css	2890
uqtestquadgraphszipfqvopf/images/0rootsPosGt0.png	2890
uqtestquadgraphszipfqvopf/images/1rootPosLt0.png	2890
uqtestquadgraphszipfqvopf/imsmanifest.xml	2890
uqtestquadgraphszipfqvopf/images/2rootsPosGt0.png	2890
uqtestquadgraphszipfqvopf/images/1rootPosGt0.png	2890
uqtestquadgraphszipfqvopf/images/2rootsNegGt0.png	2890
uqtestquadgraphszipfqvopf/images/0rootsNegLt0.png	2890
uqtestquadgraphszipfqvopf/images/0rootsNegGt0.png	2890
uqtestquadgraphszipfqvopf/images/1rootNegGt0.png	2890
uqtestquadgraphszipfqvopf/images/1rootNegLt0.png	2890
uqtestquadgraphszipfqvopf/images/0rootsPosLt0.png	2890
uqtestquadgraphszipfqvopf/images/2rootsPosLt0.png	2890
imsmanifest.xml	2890
uqtestquadgraphszipfqvopf/images/2rootsNegLt0.png	2890
uqteststrlinegdtfromanglezipvlurbf/imsmanifest.xml	2907
uqteststrlinegdtfromanglezipvlurbf/images/strlineAngleObtuse.png	2907
uqteststrlinegdtfromanglezipvlurbf/images/strlineAngleAcute.png	2907
uqteststrlinegdtfromanglezipvlurbf/images/gdtLine.png	2907
\.


--
-- Data for Name: deliveries; Type: TABLE DATA; Schema: public; Owner: qtiworks
--

COPY deliveries (did, creation_time, type, lti_consumer_key_token, lti_consumer_secret, lti_enabled, open, title, aid, dsid) FROM stdin;
98	2012-11-15 11:30:09.081	USER_CREATED	pr3avCxdWupphlwnz1NZsDsLXG8uKa0s	8FJSUOC2UOLULwHDYS8tpQCRTcpGzBHB	t	t	Mock Test 1	2296	17
101	2012-11-15 14:43:52.757	USER_CREATED	tUFn8o6eISphp4RD3HrVKPDZCU9uv96S	7ZC1u2i8BvqSVQz7T3akBSnWi6Z95WmI	t	t	Mock Test 1	2299	17
104	2012-11-16 11:35:48.393	USER_CREATED	4Sw3qwCjcvSyO3XmakqHNQRW2C9lViJ4	fcxwBqSJfEh8Huhfa7BIYWf6IZpr4ANa	t	t	Mock Test 3 - allow review	2302	17
105	2012-11-16 12:28:12.573	USER_CREATED	S0yxkKpVe4pNSNa71Pi5matUuAnSPas9	H20F1R3QoF6xG0bkdhbESWGTVbfiea7p	t	t	Mock Test - Questions in fixed order, question scores at end	2303	17
111	2012-11-16 15:55:00.672	USER_CREATED	CalUXm4UP9hCUNvEujxzXbIRLwPfmrqm	Pt5kk5Kp4wlsEk5OglzJVFMTESia0FTF	t	t	Test01 Arithmetic (no review)	2314	17
112	2012-11-16 16:02:37.23	USER_CREATED	SEVPG3IF8YIOBh7M5T6bPwgaY9hxTGTy	bITNRWRIOFpZuP8nLiLiPe9gks86SLe7	t	t	Test01 Arithmetic	2315	17
122	2012-11-16 23:16:42.312	USER_CREATED	esJABmZVyY9Zvyek10iZTVtR6InAmdNo	bzTH9fAqxVTLfIDwr8jwNv2spYscx87f	t	t	Test02 Algebra 1	2327	17
158	2012-11-20 23:58:40.215	USER_CREATED	VaWWiM5DjsEoQACcXUfUqQ2Swpp6KBcV	6q87wSTC8f89Ut7uvX584qeOzYfsB0Vj	t	t	Interpret a stem and leaf graph	2370	14
178	2012-11-22 00:06:19.013	USER_CREATED	6A1quU33CaR8E9II7bcJvveE4zwxheFq	HlvzHnQmQeqYffjO301Sn5aKTh8gvaSL	t	t	Find the mean from a frequency table	2394	14
179	2012-11-22 00:07:33.085	USER_CREATED	kyBXlZZglSHHG44TEmeaDtwyOGa83QU6	jKX2xbS45EFiUwE3axxVgQi1eGjTRv8l	t	t	Find the percentage in a given range from a frequency table	2395	14
187	2012-11-22 15:58:51.398	USER_CREATED	9LvClyyDK0fZxvjIr43vTVAts1mxKXY2	JMqHV99dlcHcSoHxSByTT2jsxNlM7fi6	t	t	Construct a frequency table	2404	14
194	2012-11-22 20:19:41.556	USER_CREATED	B8wLMXMpYZz2UaWESodOtWt4dORcQgBc	3MUt3B115eDPYSD3S9e7JcR8fzB9JU4N	t	t	Hebrew gender exercise	2411	14
196	2012-11-22 20:23:45.988	USER_CREATED	rOnudCSuTev4v9FwYTTojk1DSmorEQBC	RuhbIlOvAsnky7VwarFNHt8hw5uKHTLx	t	t	Rainbow	2413	14
202	2012-11-22 20:46:42.161	USER_CREATED	dJ1lqBTxdNXx7hoCbzqE7sdE75WfCEXb	Y79TVS6Snjt0KMJ6e7n5syT6GOv8yVbc	t	t	Noble gases	2419	14
205	2012-11-22 20:51:06.232	USER_CREATED	90uGogfoSjrSoEGmsW7g2EG9pGDgO3xG	j8a71AEYpBgCRZ9SQLc0KDAcMTghofAH	t	t	Legend	2422	14
207	2012-11-22 20:59:23.719	USER_CREATED	baTEXMxFFsPURhnhhaOTAYEIXOMCj4PW	6mgJwOZUMivLTlPUtz539IHfUcpKLWd6	t	t	Identifying polynomials	2424	14
210	2012-11-22 21:51:20.235	USER_CREATED	zKSmkPX3OJHIhwGGMStcstksNsTXU19M	GRPDEYQySjNxSKJB4Nry15nVSZ5AFVho	t	t	Demo Test for Algebra 1	2327	17
222	2012-11-24 17:48:34.841	USER_CREATED	fFtx5kiX5fxgZv4dY95smRSfP2k0VYNi	su7qIPlcSHzYfn3HO3F19GQPBC6IT6Tm	t	t	Test03 Trigonometry 1	2436	17
225	2012-11-25 20:46:16.951	USER_CREATED	L2o9py4ti9pZQnc0UPWwgMqEAxcwq4bY	Ml1DTQ96bJG8aHOrSCLMZH9U8JRKxheh	t	t	Test04 Geometry 1	2441	17
241	2013-01-03 12:59:57.588	USER_CREATED	CGmIef9VQ2YhZy7fCTGATahjSDP8KThv	Thn6l0HXxzIYMuh3IdjEURGjC640vDY0	t	t	Test 05: Statistics and Vectors	2454	17
244	2013-01-03 14:15:34.074	USER_CREATED	UT5JHIsJrxaDY7eOTmIuMNJxfwtn58c0	uW5yf8pjuik1TBKqSczeMrVTw6gdSC09	t	t	Tutorial: Stem and leaf graphs	2456	17
245	2013-01-03 14:16:47.681	USER_CREATED	S9u8jJTbRv6j5FSVmzYrR0eDSThCT71b	xCvKnINSsAAQi1fiDKsmKhQIkPQM059l	f	f	Delivery #2	2456	17
246	2013-01-03 14:34:51.798	USER_CREATED	uuEq8ahV6uL8AUKjcBYb3lMS6zylQcX5	9bCpCk6d4APq1UGFWTm6wbWyO8Uwrizr	t	t	Tutorial: Find the mean from a frequency table	2457	17
247	2013-01-03 14:36:25.09	USER_CREATED	6lO8nGKd0SHBH0kcag4XLqOYjZ5fdv9E	TmC7QeOziJ8fLGfPFoTmycoFE0fdJSrV	t	t	Tutorial: Find the percentage in a given range from a frequency table	2458	17
248	2013-01-03 14:37:00.646	USER_CREATED	JhfDJZ5Xxrwjgb42ml9uTh5K828BA3n9	qf0N3jME05Zgd6M0h4HSmWl651HfSyiH	t	t	Tutorial: Construct a frequency table	2459	17
252	2013-01-04 13:04:38.482	USER_CREATED	R71VNyNb0v7LO3jrPlgGaNq9oup65rzk	ZkGqHquigTHbwzhRNk3vswEtTKajlWt1	t	t	Test 06: Algebra and Trigonometry 2	2469	17
259	2013-01-04 18:28:00.893	USER_CREATED	p526gTIMB3SaVIGO4kRTUmXy7QsD32mQ	dleLNGwEKoGlBE7T0E7wXZdnJwouSTCv	t	t	Test 07: Geometry 2	2486	17
\.

--
-- Data for Name: test_delivery_settings; Type: TABLE DATA; Schema: public; Owner: qtiworks
--

COPY test_delivery_settings (dsid) FROM stdin;
17
\.


--
-- PostgreSQL database dump complete
--

