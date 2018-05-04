

## CURRENT
* No Changes


## coeus-1805.0007
* Revert "RESKC-2870: Fix leadUnit issue when swapping award persons roles. (#2873)" (#2875)

  * This reverts commit 137b8db0c8ef3ee42c9426084098f1b56886dfb2.  * Omar Soto Fortuño on Fri, 4 May 2018 12:26:08 -0400 [View Commit](../../commit/4f2e369ae513780f49d6d20250de883d3ceb2c29)
* RESKC-2870: Fix leadUnit issue when swapping award persons roles. (#2873)

  * Omar Soto Fortuño on Fri, 4 May 2018 09:44:42 -0400 [View Commit](../../commit/137b8db0c8ef3ee42c9426084098f1b56886dfb2)

## coeus-1805.0006
* No Changes


## coeus-1805.0005
* RESKC-2821: Relax PersonID validation on NotificationTypeRecipient. (#2871)

  * Omar Soto Fortuño on Thu, 3 May 2018 12:44:24 -0400 [View Commit](../../commit/9bc9c9e1b9a0160ee7638e0a3519dd0945e0d852)

## coeus-1805.0004
* RESKC-2858: Fix issue that created duplicated budget versions when clicking the Create Budget button multiple times. (#2870)

  * Omar Soto Fortuño on Wed, 2 May 2018 17:52:43 -0400 [View Commit](../../commit/cee319086a85ce80eb702d8388827b3ace68c6eb)

## coeus-1805.0003
* No Changes


## coeus-1805.0002
* RESKC-2726: Save in route proposal only when the form is dirty. (#2869)

  * Omar Soto Fortuño on Wed, 2 May 2018 09:25:35 -0400 [View Commit](../../commit/110ee98e8703bae8a64df8cab33407f92156ef0e)
* RESKC-2901: Add the ability to add the Full Name to the document initiator field on Document Headers. (#2868)

  * Omar Soto Fortuño on Wed, 2 May 2018 08:45:50 -0400 [View Commit](../../commit/11bf2e9664aafcc94d9499816fa95df3f3b2a39d)

## coeus-1805.0001
* RESKC-1772: Fix rounding issues on forms: Budget Summary Period (1 - 5) and Budget Summary Total from the Generic Printing Forms (Coeus 4.x) package and, create tool to generate Oracle SQL. (#2867)

  * Omar Soto Fortuño on Tue, 1 May 2018 08:28:08 -0400 [View Commit](../../commit/725b37ea670ff650d5319565757b4b5547308992)

## coeus-1804.0036
* RESKC-2910: Add PATCH support for simple crud rest endpoints (#2866)

  * Terry Durkin on Fri, 27 Apr 2018 14:39:19 -0400 [View Commit](../../commit/0593cb79dba0c08309fb9b88386861e8141a92f7)
* RESKC-2909:Avoid NPE when tests run in different order (#2865)

  * Douglas Pace on Thu, 26 Apr 2018 16:06:29 -0700 [View Commit](../../commit/68a95c24e61b0b6c1a74903fd16f8ce0dec3a13b)

## coeus-1804.0035
* Add protocol viewer roles as predefined for groups push (#2864)

  * Douglas Pace on Thu, 26 Apr 2018 13:00:08 -0700 [View Commit](../../commit/5b92ffa45f043d811a4093b0acf05447045c7568)

## coeus-1804.0034
* No Changes


## coeus-1804.0033
* RESKC-2881: Fix misplaced SQL rice code on DB scripts. (#2863)

  * Omar Soto Fortuño on Tue, 24 Apr 2018 20:27:16 -0400 [View Commit](../../commit/9146567bcf8644aff6143cd28023076f04d065b4)

## coeus-1804.0032
* RESKC-2881: Fix Narrative comments not large enough to hold Route Annotation. (#2855)

  * timbui1 on Tue, 24 Apr 2018 16:27:09 -0700 [View Commit](../../commit/50ca9b4ffb2e67288f95fa213e52555e0e596ad1)

## coeus-1804.0031
* RESKC-1772: Fix calculation issues on Budget Summary Total - Generic Print Form while using NIH as sponsor. (#2862)

  * Omar Soto Fortuño on Tue, 24 Apr 2018 16:29:27 -0400 [View Commit](../../commit/52044e9235ec55a2f7fe4192103c2928fde44d40)

## coeus-1804.0030
* RESKC-2613: Fix Oracle Scripts for Award Sequencing. (#2861)

  * Omar Soto Fortuño on Fri, 20 Apr 2018 15:57:04 -0400 [View Commit](../../commit/da40d15c0547ee94808ac3618fe844b80566b368)

## coeus-1804.0029
* RESKC-2613: Fix SQL Flywheel sequence for award sequencing migration script. (#2860)

  * Omar Soto Fortuño on Fri, 20 Apr 2018 12:45:02 -0400 [View Commit](../../commit/a262177cc9f582c24d8558d165c8a1ab9a02452c)

## coeus-1804.0028
* RESKC-2306: Added full name to the Last Updated By field on the Header of Sub Award Document.

  * Omar Soto Fortuño on Fri, 20 Apr 2018 10:39:42 -0400 [View Commit](../../commit/c767fd3f572e10d45ac2454aa2449dcb2f803cde)

## coeus-1804.0027
* RESKC-2613: Fix an issue that created a syncing problem with award_person (and other award associated tables) table when editing an award that was cancelled in the previous version. Changed how we handle sequencing when creating a new Award Version using SequenceUtils. Created integration test for this scenario. Created migration scripts to fix the issue in current data.

  * Omar Soto Fortuño on Fri, 20 Apr 2018 08:49:17 -0400 [View Commit](../../commit/ff01de2c508bb6ddbbc79022b7e49531d066dda2)

## coeus-1804.0026
* RESKC-2902: Fix Oracle integration test on a clean database (#2859)

  * Douglas Pace on Thu, 19 Apr 2018 22:08:16 -0700 [View Commit](../../commit/695755c97c3c9de2b41ef56db3d81dc8ae22e08c)

## coeus-1804.0025
* No Changes


## coeus-1804.0024
* Fix integration test. (#2857)

  * Gayathri Athreya on Tue, 17 Apr 2018 20:46:33 -0700 [View Commit](../../commit/b35be839ebe3945b45151be1c7f12824a2e7375f)
* RESKC-2898: End points for workflow stuff in portal (#2856)

  * Gayathri Athreya on Tue, 17 Apr 2018 18:38:01 -0700 [View Commit](../../commit/e92297760fa723b19dd07ee236f0230e4f1a747b)

## coeus-1804.0023
* RESKC-2860: Added link out to standalone modular budget along with pop-out link component (#2854)

  * 
  * Jeff Largent on Tue, 17 Apr 2018 12:14:55 -0400 [View Commit](../../commit/cf129cd3bc005a968fbf5e2d3cd54676b5a21e5d)

## coeus-1804.0022
* No Changes


## coeus-1804.0021
* RESKC-2893: removing certain internal attachments that should not be included for HumanSubjectsClinicalTrials form
  * Travis Schneeberger on Fri, 13 Apr 2018 16:30:59 -0400 [View Commit](../../commit/b5fc463ecbd3657f3a7b5935c9c5fde8a81332bf)

## coeus-1804.0020
* RESKC-2597: Fix adhoc routing STE (#2848)

* RESKC-2597: Fix adhoc routing STE
  * Summary: If you attempt to add an ad-hoc recipient at a non-PeopleFlow workflow stop (KEW) it will give the below STE error upon clicking the 'send ad-hoc request' button. Relates to work done in   RESKC-2226 DELIVERED  .
  * 
  * Steps to Reproduce:
  * 
  * create proposal - use environment where KEW workflow exists like in res-demo1
  * complete and route
  * approve until to KC-WKFLW OSPApprover stop
  * add an ad-hoc approver
  * click the 'send ad-hoc recipients' button and get below STE
  * Testing Information:
  * res-demo1 doc 24714
  * 
  * Actual Result: get below STE on clicking 'send adhoc requests'
  * 
  * Expected Result: should add ad-hoc recipient to workflow as expected.
  * 
* Added null check
  * Gayathri Athreya on Fri, 13 Apr 2018 10:01:40 -0600 [View Commit](../../commit/6fa6e6f4d577a94e3e95df682257c4fe00371602)

## coeus-1804.0019
* RESKC-1681: Fix an issue when trying to create a new Award Budget while being in Do Not Post status. (#2849)

  * Omar Soto Fortuño on Fri, 13 Apr 2018 10:19:08 -0400 [View Commit](../../commit/07d0fc3b132b4f3a845a96eb6a46ab384aef2f04)

## coeus-1804.0018
* RESKC-2436: Fix S2S bug when sponsor has Agency Tracking Number. (#2847)

  * Omar Soto Fortuño on Thu, 12 Apr 2018 17:36:44 -0600 [View Commit](../../commit/6a3c54be4d5e0c4d3cdfc5435b5fbd4087f5b3c1)

## coeus-1804.0017
* RESKC-2871: Certification fix (#2846)

* RESKC-2871: Certification fix
  * Summary: When the ENABLE_ADDRESSBOOK_CERTIFICATION parameter is set to N it should not require certifications for non-employee key personnel in the proposal. However, if you set to N it's still requiring and throwing a validation error prior to submit if the KEY_PERSON_CERTIFICATION_DEFERRAL is set to BS. It will require prior to person approval when set to BA. Since non-employees aren't associated with the institution and typically don't have access to the system they should not have to certify and could complicate/delay GG submissions that may contain non-employees. This may relate to recent updates to the Proposal Award Person Role table in   RESKC-2498 DELIVERED .
  * 
  * Steps to Reproduce:
  * 
  * change parameter ENABLE_ADDRESSBOOK_CERTIFICATION to N
  * change parameter KEY_PERSON_CERTIFICATION_DEFERRAL to BS
  * create a proposal with non-employees added to the Key Personnel tab (or use 14712 in res-tst1)
  * validate proposal - see certification required validations
  * Actual Result: when the ENABLE_ADDRESSBOOK_CERTIFICATION is set to N it is still requiring person certification.
  * 
  * Expected Result: when the ENABLE_ADDRESSBOOK_CERTIFICATION is set to N it will not require person certification. If set to Y it should require. Need to test with both KEY_PERSON_CERTIFICATION_DEFERRAL set to BS and BA.
  * 
* Refactored.
  * Gayathri Athreya on Thu, 12 Apr 2018 12:16:20 -0600 [View Commit](../../commit/5412902ebddc3f64ab95f8fd530861a721fb9ed9)

## coeus-1804.0016
* RESKC-1570: Fix fringe benefits calculation on Budget CostShare Summary Report and create a Test for it. (#2845)

  * Omar Soto Fortuño on Thu, 12 Apr 2018 09:33:33 -0600 [View Commit](../../commit/ad04655ccd9ab34a11bf7a9ba0b3c0b07b64514b)

## coeus-1804.0015
* RESKC-2182: Update Rice version for priority parallel routing fix
  * Jeff Largent on Tue, 10 Apr 2018 14:24:20 -0400 [View Commit](../../commit/19df60f8895cc0dbe4cdc93f0b5faa9d097a89e3)

## coeus-1804.0014
* RESKC-2877: handling attachments that don't use the common attachment namespace directly.
  * Travis Schneeberger on Mon, 9 Apr 2018 21:00:48 -0400 [View Commit](../../commit/497c349dc1725a0868aca1a7702572fad5b5162c)

## coeus-1804.0013
* RESKC-2857: supporting PI contacts for FDP
  * Travis Schneeberger on Mon, 9 Apr 2018 13:36:51 -0400 [View Commit](../../commit/807395fab2a95fadf7159cf916fd07ef3bf8888c)

## coeus-1804.0012
* RESKC-2268: Ensured that calculated amounts are reset when Award Budget rate type changes
  * Jeff Largent on Fri, 6 Apr 2018 15:34:28 -0400 [View Commit](../../commit/e4d28a85b2e2d360da52571e02ebcece7da68c2b)

## coeus-1804.0011
* RESKC-2853: Change validation pattern for opportunity title field on Institutional Proposals so, it will be the same as it’s in Development Proposal. Same fix as done in RESKC-2188. (#2840)

  * Omar Soto Fortuño on Fri, 6 Apr 2018 14:04:03 -0400 [View Commit](../../commit/82e76624f9bb6d4ac50a83235158a360ba2591f7)

## coeus-1804.0010
* RESKC-1875: Fixed bug that allowed SubAward Viewer to see the Delete and Replace buttons for the attachments. (#2839)

  * Omar Soto Fortuño on Fri, 6 Apr 2018 12:08:51 -0400 [View Commit](../../commit/da0ebda3d6280c750f74e323aabeb12c5b143c85)

## coeus-1804.0009
* RESKC-2869: TBN fix (#2834)

  * Found that adding TBNs with certain descriptions is causing a STE. Specifically if named like below:
  * TBA-Graduate Assistant
  * TBA-Grad or Undergrad Student
  * TBA-Postdoctoral Fellow
  * TBA-Classified Staff
  * TBA-Unclassified Staff
  * TBA-Faculty
  * 
  * Doesn't seem to like the '-' in the name of the TBN; if you attempt to add more than one or an additional one it will go to below error.
  * 
  * Steps to Reproduce:
  * 
  * create proposal (or use proposal 11844 in res-tst1)
  * add budget
  * add TBNs to Project Personnel that contain '-' (more than one or one and then another) and get STE  * Gayathri Athreya on Fri, 6 Apr 2018 08:50:55 -0700 [View Commit](../../commit/fc866531538a5d374cb60e77d5478ceb8d85d083)
* RESKC-2871: Fix certification (#2837)

  * If in the Proposal Award Person role table, the certification requirement for co-investigators is turned off - the system is still throwing an error in PD requiring the certification.
  * 
  * Steps to Reproduce:
  * (Configure the Proposal Award Person Role maintenance table so that Certification is not required for Co-Investigators)
  * 1. Create a PD with min. info to save
  * 2. Navigate to Key Personnel section and add a PI and at least one Co-I
  * 3. Run Data Validation
  * 
  * Actual Result: You will see the following Error(s) for those users added as Co-Investigators: The Investigators are not all certified. Please certify xxxxxx.
  * 
  * Expected Result: No error message should result for Co-Investigators since they are not required to certify based on the configuration in the Proposal Award Person role table)  * Gayathri Athreya on Fri, 6 Apr 2018 08:50:17 -0700 [View Commit](../../commit/1ce610c77983801247185e850de563327ab84a7e)
* RESKC-2299: SUMMARY: Discovered that if an ad-hoc approver attempts to use the 'return' option to send it back to the initiator it will give them a STE and not return the proposal  Not sure if this is caused by some lack of permission setup or some larger issue - you would think a user being ad-hoc routed to with the 'return' button available would be able to use without issue or config needed. (#2838)

  * STEPS TO REPRODUCE:
  * 1. create a proposal with all required fields to route (or use an existing enroute proposal like doc 97158)
  * 2. open proposal and go to the Summary/Submit tab
  * 3. click Ad Hoc Recipients button
  * 4. add 'tdurkin' as an ad hoc recipient for Approve; click 'ok'
  * 5. click Send Adhoc button and open workflow to verify it's now in tdurkin's action list for approve
  * 6. login as tdurkin and open proposal
  * 7. click 'return' and include text on description and click 'ok' - get STE
  * 
  * TEST INFO:
  * res-tst1 document number: 97158
  * 
  * ACTUAL RESULTS: the proposal does not return and gives user STE - prevents return action
  * 
  * EXPECTED RESULTS: the proposal successfully returns to the initiator as expected.  * Gayathri Athreya on Fri, 6 Apr 2018 08:49:46 -0700 [View Commit](../../commit/d97c4c05b63ad59269783f8d5e9169db736cf97d)
* RESKC-2307: Added full name to the Pessimistic Lock message. (#2835)

  * Omar Soto Fortuño on Fri, 6 Apr 2018 10:58:35 -0400 [View Commit](../../commit/5489edef35562b28c93b4a079448f0a9be49691c)

## coeus-1804.0008
* RESKC-2154: Made sure that budget periods are correctly re-sequenced when one is deleted

* Also made sure that KRAD doesn't try do an AJAX refresh after adding a budget period, since this can overwrite the newly added row.
  * Jeff Largent on Thu, 5 Apr 2018 15:36:37 -0400 [View Commit](../../commit/075be74debe9b408b793415f4085404ebfebd0a0)

## coeus-1804.0007
* No Changes


## coeus-1804.0006
* No Changes


## coeus-1804.0005
* RESKC-2696: Made sure that latest version of funding proposals are are associated with award (#2833)

  * Jeff Largent on Wed, 4 Apr 2018 16:40:39 -0400 [View Commit](../../commit/045e55ab491a8c1f811fbbac1713f6dc7944f732)
* RESKC-2299: Fixing STE when adhoc approver rejects document. (#2832)

  * Gayathri Athreya on Wed, 4 Apr 2018 13:00:12 -0700 [View Commit](../../commit/0a79bb5d11a5f5e63385ed53c29a02fced13f7ce)

## coeus-1804.0004
* RESKC-2306: Added full name to the Last Updated By field on the Header of Award Document and Time and Money Document.

* RESKC-2306: Added full name to the Last Updated By field on the Header of Award Document and Time and Money Document.  * Omar Soto Fortuño on Wed, 4 Apr 2018 13:48:18 -0400 [View Commit](../../commit/47ae44a46cfa326d06167e41d3fd5de146bea064)

## coeus-1804.0003
* No Changes


## coeus-1804.0002
* get inactive ppl from core (#2830)

  * Terry Durkin on Tue, 3 Apr 2018 11:19:19 -0400 [View Commit](../../commit/df7657ee5113270fdefceaa49139ca143156d04d)
* RESKC-2868: making stage and group optional
  * Travis Schneeberger on Tue, 3 Apr 2018 10:55:14 -0400 [View Commit](../../commit/258e2e7fc90fcee37cf7c4fae52c1d052f211a93)
* RESKC-2868: test to make sure we handle mix-case usernames
  * Travis Schneeberger on Tue, 3 Apr 2018 10:40:31 -0400 [View Commit](../../commit/5ec95db4ef18a414074eeae42df180e6f3006fdb)

## coeus-1804.0001
* REKC-1010: Increase set of characters filenames can have (#2827)

  * Gayathri Athreya on Mon, 2 Apr 2018 07:34:41 -0700 [View Commit](../../commit/620f0c931d6ef26c4ee08d57d3cae7a569051baf)

## coeus-1803.0045
* RESKC-2865:Fix dead lock when installed on empty database (#2828)

  * Douglas Pace on Sat, 31 Mar 2018 16:14:37 -0700 [View Commit](../../commit/5d4f14349c02256dfd3fe4fc4919c20dd8fa25a9)

## coeus-1803.0044
* RESKC-2110: fix subaward peopleflow for retrieving unit number
  * Travis Schneeberger on Fri, 30 Mar 2018 13:55:22 -0400 [View Commit](../../commit/4d6b3c55def232f657e7fe54e49090ab0681c734)
* RESKC-2110: add peopleflow support for Award
  * Travis Schneeberger on Fri, 30 Mar 2018 13:54:43 -0400 [View Commit](../../commit/cae280d0538aaec7a303d8fd611dcce356683eb9)

## coeus-1803.0043
* No Changes


## coeus-1803.0042
* RESKC-1497: Ensure all opportunity info gets copied to proposal when initiating from opportunity lookup
  * Jeff Largent on Thu, 29 Mar 2018 14:41:11 -0400 [View Commit](../../commit/a2cfaa2345cac2632656c78c8c68220bc7285831)

## coeus-1803.0041
* Fix failing tests. (#2824)

  * Gayathri Athreya on Thu, 29 Mar 2018 11:44:36 -0700 [View Commit](../../commit/cad3b5899aa6d015590997ea260da0fe06aeef6b)
* RESKC-2838: a variety of fdp printing fixes

* move the sponsor attachments panel up, so that the onscreen display order for attachment 2 is after the coverpage/modifications and attachment 1
* ensure that all PDF attachments appear in the subaward attachments page, based on the subaward inclusions parameter
* use the subaward inclusions parameter to inform the display order onscreen- descend order on left column first, then right (same order as the 3A, 3B attachments above)
* display the attachment type as well as the attachment description in the print panel.
* the pdf printout order should be the same as the onscreen order - so that Agreements/Modifcations first,  sponsor attachment, then rest of agreement attachments, and then the user added sub attachments (again, in order defined in the subaward inclusion parameter)
  * Travis Schneeberger on Thu, 29 Mar 2018 13:39:31 -0400 [View Commit](../../commit/ca503d766c91cb7d532ff1f782429a471615de29)
* RESKC-1599: Parameterized proposal type codes excluded from pending reports
  * Jeff Largent on Thu, 29 Mar 2018 12:04:15 -0400 [View Commit](../../commit/0374a9a27ec0d6c30334ee9b58571a2656864371)
* RESKC-2456: Adding authorization and tests (#2821)

* RESKC-2456: Adding authorization and tests
  * 
* Changes according to review comments.
  * Gayathri Athreya on Thu, 29 Mar 2018 09:01:45 -0700 [View Commit](../../commit/c6a7e063b0fbe3065f7858740251ff1cd8528d48)

## coeus-1803.0040
* RESKC-1996:Fix exceptions by excluding roletype name from roles and restricting people criteria (#2813)

  * Douglas Pace on Wed, 28 Mar 2018 12:53:00 -0700 [View Commit](../../commit/dde1e5ac0ad6a92116c1ecaacb9b8db796f39a9d)

## coeus-1803.0039
* No Changes


## coeus-1803.0038
* RESKC-2354: Make unrecovered F&A validations clearer and make sure they are hard audit errors
  * Jeff Largent on Tue, 27 Mar 2018 17:13:39 -0400 [View Commit](../../commit/f442606089e4abe423173372640a03688d77de0f)

## coeus-1803.0037
* RESKC-2843: Replace segment integration with direct Google Analytics
  * Jeff Largent on Tue, 27 Mar 2018 11:31:54 -0400 [View Commit](../../commit/c06645fba88a041e6abe139f054ac107f530bfcf)

## coeus-1803.0036
* No Changes


## coeus-1803.0035
* RESKC-1768: Fixing KP Cert authorization (#2814)

  * Gayathri Athreya on Mon, 26 Mar 2018 11:22:08 -0700 [View Commit](../../commit/6d5330dcce6009d4419d7ec41adcd7c4391dc6f5)
* RESKC-2807: Ensure final budget reference is removed before deleting proposal
  * Jeff Largent on Mon, 26 Mar 2018 13:46:07 -0400 [View Commit](../../commit/0bbf64617c7c4bdb807ecf3f245639899b8b2b0d)

## coeus-1803.0034
* No Changes


## coeus-1803.0033
* RESKC-2711: Made sure proposal number is included in mod budget API
  * Jeff Largent on Mon, 26 Mar 2018 09:41:31 -0400 [View Commit](../../commit/2106f21383532808974b9495d94de9d32cd6af0c)
* RESKC-2291: CSU Contribution: Fixed bug where fiscal year was compared to calendar year in getcurrentfandarate method. (#2815)

  * (cherry picked from commit f5685d5)  * Tyler Wilson on Mon, 26 Mar 2018 07:38:59 -0600 [View Commit](../../commit/0a5d09c3a07673432d7656df0257b44c70cc776d)

## coeus-1803.0032
* No Changes


## coeus-1803.0031
* upgrade s2s (#2811)

  * Gayathri Athreya on Fri, 23 Mar 2018 10:25:30 -0700 [View Commit](../../commit/6c10049d7b089c94e305a8b4ad7d09e6069e25eb)
* RESKC-2596: making sure approval comments display when warnings exist and proposal.approval.dialog.enabled is on
  * Travis Schneeberger on Fri, 23 Mar 2018 12:57:16 -0400 [View Commit](../../commit/2f21d7faa77cc77c8176b548db99620995e7b39a)
* RESKC-2561: Fixed IP delete button issue. (#2810)

  * Gayathri Athreya on Fri, 23 Mar 2018 09:45:33 -0700 [View Commit](../../commit/f7f3a9288c40b513a20ef0f87f95abdd45e4dc77)
* RESKC-2396: Fix STE while adding TBNs (#2808)

  * Gayathri Athreya on Fri, 23 Mar 2018 09:33:33 -0700 [View Commit](../../commit/c579fd6caa97704e0f5eebd47a6b25e1e0540ec6)

## coeus-1803.0030
* RESKC-1830: only allow prop dev delete when status is In Progress
  * Travis Schneeberger on Fri, 23 Mar 2018 11:01:35 -0400 [View Commit](../../commit/dba13d398ee5e04a73391ed2105b6dc25dddeb0b)
* RESKC-2557: checking for lead unit on copy
  * Travis Schneeberger on Thu, 22 Mar 2018 16:06:01 -0400 [View Commit](../../commit/55ba1baf34ab9149497af6643e5f9bf4d9952b7b)

## coeus-1803.0029
* RESKC-2846: switch citi processing to incremental saves
  * Travis Schneeberger on Thu, 22 Mar 2018 10:37:29 -0400 [View Commit](../../commit/3d77b0d34bbba3160093c5bfa50fd47f0e2cc78c)

## coeus-1803.0028
* RESKC-2711: Removing unused constants and interface methods
  * Jeff Largent on Tue, 20 Mar 2018 15:26:16 -0400 [View Commit](../../commit/136d0a7c8605e0a081a58d57c24752d191429f34)
* RESKC-2711: Check authorization for modular budget API and remove config endpoint
  * Jeff Largent on Tue, 20 Mar 2018 15:18:32 -0400 [View Commit](../../commit/6ef25e210d4751a816fc678d35afc9f703cd6cf6)
* RESKC-2836: minor UI improvements
  * Travis Schneeberger on Tue, 20 Mar 2018 12:54:17 -0400 [View Commit](../../commit/698f988e096f3700a085708b5ae55cd37e85331a)

## coeus-1803.0026
* RESKC-2836: fixing test
  * Travis Schneeberger on Tue, 20 Mar 2018 08:52:06 -0400 [View Commit](../../commit/c25dffd5f18d2a72dd37631333705f20c65d45f3)

## coeus-1803.0025
* RESKC-2836: proposal opportunity search screen improvements
  * Travis Schneeberger on Mon, 19 Mar 2018 17:00:10 -0400 [View Commit](../../commit/ce2a17731ab4ffcbd0e2679735d905a3ea8e2b0e)

## coeus-1803.0024
* RESKC-1494: Fix TBN calculations (#2798)

  * When a Budget includes more than one TBA, each with different Salary Effective Date, the system is confusing the TBAs and pulls the wrong Base Salary for calculating the Requested Salary amount.
  * 
  * For example, in Proposal No: 1499 in res-demo1:
  * 1. The Proposal Period is 07/01/2016 - 06/31/2017
  * 
  * 2. In the budget > Project Personnel, I added 2 TBA Research:
  * 
  * TBA Research - 1 with Salary Effective Date 07/01/2015 and Base Salary of $10,000
  * TBA Research - 2 with Salary Effective Date 07/01/2016 and Base Salary of $20,000
  * 3. In the Assign Personnel to Periods section, when I budgeted for TBA Research -1, the system used the Base Salary of $20,000 (which is the Base Salary of the TBA Research - 2) to calculate the Requested Salary, INSTEAD of using the Base Salary of $10,000
  * (This is happening with all TBN categories, for example, if user has TBA Research - 1 and TBA Post-Doc-1 with different salary effective dates, the system will also confuse the two even-though they are different TBN categories)
  * 
  * Steps to reproduce:
  * 
  * 1. Create a Proposal (e.g. Project Period: 07/01/2016 - 06/30/2017)
  * 
  * 2. Create a new, detailed Budget version.
  * 
  * 3. In the Project Personnel section, add 2 TBA Research.
  * 
  * 4. Click the [Details] button for the TBA Research - 1 and complete as follows:
  * a. Salary Effective Date: 07/01/2015
  * b. Base Salary: 10000
  * c. Click the [Save Changes] button
  * 
  * 5. Then click the [Details] button for the TBA Research - 2 and complete as follows:
  * a. Salary Effective Date: 07/01/2016
  * b. Base Salary: 20000
  * c. Click the [Save Changes] button
  * 
  * 6. Navigate to the Assign Project Personnel to Periods section and click the [Assign Personnel...] button. In the Add Personnel to Period window select/enter:
  * a. Person: TBA Research - 1
  * b. Object Code: Research Staff - On
  * e. Effort %: 100
  * f. Charged %: 100
  * h. Click the [Assign to Period 1] button
  * 
  * 7. You will notice that the Requested Salary amount that is calculated, is based on the Base Salary for TBA Research - 2 INSTEAD of the Base Salary for TBA Research - 1  * Gayathri Athreya on Mon, 19 Mar 2018 13:25:48 -0700 [View Commit](../../commit/f85767cca345f28f4aa682c620d899a39a40314f)
* RESKC-2625: Ensure obligation-related award notice params are being populated correctly
  * Jeff Largent on Mon, 19 Mar 2018 13:07:18 -0400 [View Commit](../../commit/65831e69f5a0bc801d294fd2b9dea8045c94d543)

## coeus-1803.0023
* RESKC-841: removing logic to populate division when adding a proposal person, updating s2s
  * Travis Schneeberger on Fri, 16 Mar 2018 16:20:11 -0400 [View Commit](../../commit/0e9f01c20ba84c99202c7b1bceaf1d9e3ec924e8)

## coeus-1803.0022
* RESKC-2454: Added a couple additional properties and updated config route
  * Jeff Largent on Fri, 16 Mar 2018 14:48:10 -0400 [View Commit](../../commit/aee7a5d10818a24b0cf2c48c0ecdc5ee7d9adbc0)
* RESKC-2454: Adding info required by modular budget to monolith API
  * Jeff Largent on Fri, 16 Mar 2018 09:26:28 -0400 [View Commit](../../commit/bebce27e52bfc5262257ab193159b826a17dd7c3)

## coeus-1803.0021
* RESKC-2836: handling pdf file attachments in user attached forms properly.
  * Travis Schneeberger on Thu, 15 Mar 2018 14:39:14 -0400 [View Commit](../../commit/3e601d6a3dd6b54cf472ee2b4df7176ced0ed722)

## coeus-1803.0020
* No Changes


## coeus-1803.0019
* RESKC-2836: making sure fileName and href match to satisfy grants.gov rules, making sure a hashValue exists for each attachment
  * Travis Schneeberger on Wed, 14 Mar 2018 14:32:06 -0400 [View Commit](../../commit/bbbcea25e075e791c928dba0d90071ba753a5c9b)
* RESKC-2836: minor fixes
  * Travis Schneeberger on Wed, 14 Mar 2018 11:14:23 -0400 [View Commit](../../commit/3e00f8d43b5b4743461600f7dd7872a1e6670039)

## coeus-1803.0018
* No Changes


## coeus-1803.0017
* RESKC-2830: making the font smaller on the bilateral form.
  * Travis Schneeberger on Tue, 13 Mar 2018 11:29:37 -0400 [View Commit](../../commit/9b0e94b541a1017c39f022e8ce2c350c06643bcc)

## coeus-1803.0016
* No Changes


## coeus-1803.0015
* No Changes


## coeus-1803.0014
* RESKC-2491: Ensure that associated schedules get deleted along with unsaved reports
  * Jeff Largent on Fri, 9 Mar 2018 13:34:44 -0500 [View Commit](../../commit/c20d708e64b80135bf211f6f90dc24c00e41bb91)
* RESKC-2810: Make "Final" default document status for SubAward lookup
  * Jeff Largent on Fri, 9 Mar 2018 12:56:20 -0500 [View Commit](../../commit/0bc3982a19a6f6df5edcd3d208fecace213d4445)

## coeus-1803.0013
* RESKC-1844: Don't update timestamp / user when T&M docs are archived
  * Jeff Largent on Fri, 9 Mar 2018 10:36:14 -0500 [View Commit](../../commit/667aa2ee8ac8f4cd2ca09fc33a045ecfde49d502)

## coeus-1803.0012
* No Changes


## coeus-1803.0011
* RESKC-1786: Fixed issue where inflation rate was being applied twice...

  * ...when a person's effective date occurred before the period start date and their salary anniversary date was between (or on) the effective date and period start date.
  * Jeff Largent on Thu, 8 Mar 2018 16:48:00 -0500 [View Commit](../../commit/25dafea6b6323edc11a4a33d792f040d4ca838f8)
* RESKC-2810: Minor contribution cleanup
  * Jeff Largent on Wed, 7 Mar 2018 22:50:10 -0500 [View Commit](../../commit/5aa7ea8b4c72201df34afb00d22ff16c0ee7bdb2)
* [#] Added 'Document Status' field to SubAward lookup and results
  * rojlarge on Wed, 20 Jan 2016 21:48:22 -0500 [View Commit](../../commit/af91a5603a4cffe334649b16839b8decf225f9bb)

## coeus-1803.0010
* No Changes


## coeus-1803.0009
* RESKC-2828, RESKC-2829: various fdp fixes
  * Travis Schneeberger on Wed, 7 Mar 2018 09:19:10 -0500 [View Commit](../../commit/729f3d8742bb8d8c21fd5f99c4833cd8c78810e2)

## coeus-1803.0008
* RESKC-2771: sending page count to NIH validation service for attachments
  * Travis Schneeberger on Tue, 6 Mar 2018 16:04:41 -0500 [View Commit](../../commit/5ac0b68fda4195114ac62470e962815488181c9c)

## coeus-1803.0007
* RESKC-2827: allow single quotes in email addresses (#2785)

  * Terry Durkin on Mon, 5 Mar 2018 12:41:57 -0500 [View Commit](../../commit/b55b355ba03bb92381928c8b1f152a5554ebb51c)

## coeus-1803.0006
* RESKC-2808: Allow dashes in notification type action codes
  * Jeff Largent on Mon, 5 Mar 2018 09:45:57 -0500 [View Commit](../../commit/887c30eb7001ddc0603277fef3d4189e12935383)

## coeus-1803.0005
* No Changes


## coeus-1803.0004
* RESKC-2818, RESKC-2819: fdp mapping fixes
  * Travis Schneeberger on Fri, 2 Mar 2018 16:09:39 -0500 [View Commit](../../commit/d0489f0200d90cc70d6f67e68ac0aa1567fbdf29)
* RESKC-2081: Allow PD applicant organization to be changed
  * Jeff Largent on Fri, 2 Mar 2018 11:10:08 -0500 [View Commit](../../commit/00b180e4bdbafaf4dd8483c94cfc19732ac87b56)

## coeus-1803.0003
* No Changes


## coeus-1803.0002
* remove noisy debug logging
  * Travis Schneeberger on Thu, 1 Mar 2018 14:21:08 -0500 [View Commit](../../commit/8730074c29ea0ef0729962c102b751271b0d1784)

## coeus-1803.0001
* Fixing broken test (#2779)

  * Gayathri Athreya on Thu, 1 Mar 2018 10:17:21 -0700 [View Commit](../../commit/4bbde7f09672fed6ad1d5e6240c34298f2a0669f)
* Mod budget api (#2776)

* RESKC-2453: Adding api for mod budget
  * 
* Moving and refactoring
  * 
* RESKC-2453: Refactoring
  * 
* RESKC-2543: Review comments
  * Gayathri Athreya on Thu, 1 Mar 2018 08:52:37 -0700 [View Commit](../../commit/57c8580c452b0951a3494b1fb705b50e5dc48e1e)
* RESKC-2722: Better error messages for NSF forms
  * Jeff Largent on Thu, 1 Mar 2018 10:47:40 -0500 [View Commit](../../commit/6b9c41a84ec79bc3fc4d34e74de2f61508bbebb1)

## coeus-1802.0042
* RESKC-2813: fdp dynamic field sizing
  * Travis Schneeberger on Wed, 28 Feb 2018 15:57:57 -0500 [View Commit](../../commit/7fd1c7a621541702559032dcc9e24ab2ff544842)

## coeus-1802.0041
* Updating footer. (#2774)

  * Gayathri Athreya on Wed, 28 Feb 2018 11:37:33 -0700 [View Commit](../../commit/201086f0867a889e1136ac2d26cad992cdb26088)

## coeus-1802.0040
* RESKC-2811, RESKC-2812, RESKC-2813: fdp fixes
  * Travis Schneeberger on Wed, 28 Feb 2018 12:21:06 -0500 [View Commit](../../commit/097493822e9968ee31706a8ca9e10fe52cc59f3b)
* RESKC-2792: Combine rolodex and employee emails before calling KcEmailService

* This prevents the email service from being called twice (once with rolodex emails, once with employee emails), which can interfere with logic to email a default address when there are no valid recipients for a notification.
  * Jeff Largent on Wed, 28 Feb 2018 10:02:18 -0500 [View Commit](../../commit/897cddf81b95c226f6aabb2ac3193c8118399248)

## coeus-1802.0038
* RESKC-841: update s2s, remove unused parameter
  * Travis Schneeberger on Tue, 27 Feb 2018 14:51:47 -0500 [View Commit](../../commit/6d98be92fb8ce00434997b9d59860098f6a0041c)

## coeus-1802.0037
* No Changes


## coeus-1802.0036
* No Changes


## coeus-1802.0035
* RESKC-2796,RESKC-2798,RESKC-2799: fdp fixes
  * Travis Schneeberger on Mon, 26 Feb 2018 10:53:55 -0500 [View Commit](../../commit/2156bbb04a5361f310b8060c3810124c02f11bd1)
* RESKC-2792: Only send real emails in Prod environments

* This change ensures that real emails cannot be sent in non-Prod environments, and aggregates all email related parameter checks in `KcEmailServiceImpl`.
  * Jeff Largent on Mon, 26 Feb 2018 11:30:15 -0500 [View Commit](../../commit/a55199d89dde557189a1b6b96b628ca14f3c54cd)

## coeus-1802.0034
* RESKC-2078: update s2s, add tests
  * Travis Schneeberger on Fri, 23 Feb 2018 13:53:21 -0500 [View Commit](../../commit/3289940adca59fcf8f02f7dd278fb948335940a5)

## coeus-1802.0033
* RESKC-2491: Delete payment schedules when associated term is deleted
  * Jeff Largent on Fri, 23 Feb 2018 09:45:25 -0500 [View Commit](../../commit/4d8e83d56818b9ecc45068b00ace78a30f712645)

## coeus-1802.0032
* No Changes


## coeus-1802.0031
* RESKC-2785: Ensure internal attachment errors display when submitting to S2S
  * Jeff Largent on Thu, 22 Feb 2018 10:55:07 -0500 [View Commit](../../commit/af6af4b8f06ace3c551cff57f443169ce8e3ed74)

## coeus-1802.0030
* RESKC-2791: Update ART notifications param to send weekly
  * Jeff Largent on Wed, 21 Feb 2018 14:12:26 -0500 [View Commit](../../commit/fbd76245fe6169cd58023200bc2024bf3826e10a)

## coeus-1802.0029
* RESKC-2766: Change overdue to a dynamically calculated property
  * Jeff Largent on Tue, 20 Feb 2018 13:54:00 -0500 [View Commit](../../commit/234e82bdd018f835f228f4e777569558df9abb7d)

## coeus-1802.0028
* RESKC-2729: Fix ART ITs to abide by new constraints
  * Jeff Largent on Tue, 20 Feb 2018 12:03:04 -0500 [View Commit](../../commit/5a82545131e5b383537b12d27def294ffadd4e40)
* RESKC-2729: Clean up report tracking data that violates referential integrity

  * These scripts clean up report tracking data that was unable to to be re-mapped to the report terms associated with the latest award versions as part of commit 37e771ee9b066fc541a2bb8c39952adbf05feecd. A lot of this data was likely duplicated due to consistency issues in legacy report tracking code, such as tracking data being incorrectly copied across versions and failing to properly update report tracking properties when the associated award and award term properties were changed. Report tracking data that was unable to be adapted to the new consistency constraints will be moved to a new `award_report_tracking_backup` table. That way these entries can be examined to see if they need to be manually re-associated with award report terms or if they can be safely ignored/deleted.
  * Jeff Largent on Fri, 16 Feb 2018 16:23:35 -0500 [View Commit](../../commit/9a2c4c1cf3b6822caefb36b19f69fab068d45f09)

## coeus-1802.0027
* RESKC-2759: Added report.finalFlag as ART lookup criteria
  * Jeff Largent on Thu, 15 Feb 2018 16:52:40 -0500 [View Commit](../../commit/aeaa8106966a1d567c8e9b7b01e95b6179e40618)

## coeus-1802.0026
* No Changes


## coeus-1802.0025
* RESKC-2680: Ensure that digest parameter lists always render in lexicographical order
  * Jeff Largent on Thu, 15 Feb 2018 11:34:27 -0500 [View Commit](../../commit/5512a2653a3cb052b1c0c32c4d110fd16d606751)
* RESKC-2680: Fix typo in parameter value
  * Jeff Largent on Thu, 15 Feb 2018 10:50:17 -0500 [View Commit](../../commit/d372d9d42a9146f5fb73c806db389757a49dfe44)
* RESKC-2680: Moved trigger enabled param to spring definition
  * Jeff Largent on Thu, 15 Feb 2018 10:20:05 -0500 [View Commit](../../commit/2fde4fd08e1b7efca65a11a10893a8be9ddb0676)
* RESKC-1680: Added Oracle scripts
  * Jeff Largent on Wed, 14 Feb 2018 15:36:06 -0500 [View Commit](../../commit/411e0e93f13c8fdb3eea32ec99cb2f4437db7b58)
* RESKC-2680: Added tests and parameter for ascending unit hierarchy for unit admin digests
  * Jeff Largent on Wed, 14 Feb 2018 14:41:44 -0500 [View Commit](../../commit/4a31a3b63ec75cc55384b331f5d13e3a06b7fa5b)
* RESKC-2680: Added ART digest notification for Unit Admins

* Also added the ability for the AwardAllUnitAdministratorDerivedRoleTypeService to ascend the hierarchy if an appropriate `subunits` qualifier is provided.
  * Jeff Largent on Tue, 13 Feb 2018 13:35:42 -0500 [View Commit](../../commit/2aa6a028c3e59bb3507f82eda5c41871f623ee2b)

## coeus-1802.0024
* RESKC-2732: Adding the FF around the lock creating code as well (#2758)

  * Gayathri Athreya on Wed, 14 Feb 2018 17:21:12 -0700 [View Commit](../../commit/a0244e145d1aa1f03ee55d6a06a893f49323f6c0)

## coeus-1802.0023
* fixing Serialization issues.
  * Travis Schneeberger on Tue, 13 Feb 2018 19:20:58 -0500 [View Commit](../../commit/2d4681f5001cdfb3cb4518baf770b27ebc481af2)
* preventing NPE when missing inflation rate type during budget calculation
  * Travis Schneeberger on Tue, 13 Feb 2018 19:20:37 -0500 [View Commit](../../commit/2a5263074f8b11c2ba1acdc6dc80b9d4f7802f07)
* preventing NPE on certain KRAD lookups
  * Travis Schneeberger on Tue, 13 Feb 2018 19:19:20 -0500 [View Commit](../../commit/1ea3c078e904a8d5061afb1b81a4af7eb3c7e157)

## coeus-1802.0022
* No Changes


## coeus-1802.0021
* RESKC-2343: add test
  * Travis Schneeberger on Mon, 12 Feb 2018 11:48:21 -0500 [View Commit](../../commit/6525607110886d25574cf37ef20da491f14c1c3e)

## coeus-1802.0020
* No Changes


## coeus-1802.0019
* RESKC-2478: updating s2s  * Travis Schneeberger on Fri, 9 Feb 2018 16:04:39 -0500 [View Commit](../../commit/3c2920b9c1435f19fd465148d6a2beccaf9d9678)
* RESKC-2681: Fix Oracle script name
  * Jeff Largent on Fri, 9 Feb 2018 15:48:01 -0500 [View Commit](../../commit/f5aadf8e97f62cddca355c9dbb91c06c1ed63783)
* RESKC-2772: Make sure to update award-related properties on reports

  * This ensures that changes to the PI or Sponsor after reports are created or copied from another Award are reflected in the Report Tracking search screen.
  * Jeff Largent on Fri, 9 Feb 2018 12:04:56 -0500 [View Commit](../../commit/ffca8acef4ef808c6e825fd9caaeb20dd3862bf3)

## coeus-1802.0018
* No Changes


## coeus-1802.0017
* RESKC-2651: fdp fixes
  * Travis Schneeberger on Fri, 9 Feb 2018 12:24:47 -0500 [View Commit](../../commit/785ba1fce1c646b0ba6704978e66fab033902894)
* RESKC-2681: Fix IT
  * Jeff Largent on Fri, 9 Feb 2018 11:43:50 -0500 [View Commit](../../commit/c0b3e0511f36f715d56922fe702fc5f94c341675)
* RESKC-2681: Added Award Report Tracking digest notification for PIs (#2748)

  * This notification can be configured to send a digest email to PIs or other recipients on any Quartz-schedulable interval to inform them of any due or overdue reports associated with awards relevant to them. 
  * Jeff Largent on Fri, 9 Feb 2018 11:06:40 -0500 [View Commit](../../commit/93f296573abfde57383338de3ec9a8982b617097)

## coeus-1802.0016
* RESKC-2724: Adding generator tests
  * Travis Schneeberger on Thu, 8 Feb 2018 13:18:35 -0500 [View Commit](../../commit/43c1f68ed44287e8e2702ff47eab3173caf4f07c)

## coeus-1802.0015
* replace xalan with official xpath in the jdk
  * Travis Schneeberger on Wed, 7 Feb 2018 16:42:50 -0500 [View Commit](../../commit/d3e09c0f9d3cb3b795b263133ab4478ba1e43ade)
* RESKC-2724: support Other form 1.2
  * Travis Schneeberger on Wed, 7 Feb 2018 16:35:30 -0500 [View Commit](../../commit/778aa652272d2df403cc4883f4bbf822c3a7e8c8)

## coeus-1802.0014
* RESKC-2607:When versioning an IP from PD the Proposal Type is Changed by
  * PD.IP version should pull all data except the Proposal Type.  * vineeth on Tue, 6 Feb 2018 15:47:01 -0500 [View Commit](../../commit/1e38449d709fa2c6ea4858e298d06e0195dc77d7)

## coeus-1802.0013
* No Changes


## coeus-1802.0012
* RESKC-2732: Prevent duplicate version creation in award, subaward and IP (#2745)

* RESKC-2732: Prevent duplicate version creation in award, subaward and IP
  * 
* Refactored
  * Gayathri Athreya on Wed, 7 Feb 2018 07:45:43 -0700 [View Commit](../../commit/224fb86a5a7a485cfce25c4f4598331e5f42babe)
* RESKC-2721: code cleanup
  * Travis Schneeberger on Wed, 7 Feb 2018 09:38:44 -0500 [View Commit](../../commit/0f4c34d7c62d3067791ea06b0f4a6e21f6c93e8c)
* RESKC-2721: Fixed logic that resulted in NPE when deleting personnel
  * Tyler Wilson on Wed, 17 Jan 2018 12:09:35 -0700 [View Commit](../../commit/86e128c993cad9ab0610c7466e0bfc15e06f6126)
* RESKC-1813: Updated files per PR comments
  * Tyler Wilson on Wed, 2 Aug 2017 15:49:52 -0600 [View Commit](../../commit/9734e190cf77c5737dccb1998081e2e212e0b4e3)
* RSISSUES-332: Updated contrib with upstream/master and proposal award lookup fix
  * Tyler Wilson on Wed, 2 Aug 2017 13:06:54 -0600 [View Commit](../../commit/545ee5ee794ad0a7f432d9286d16d2b2a7ead4cd)
* RESKC-111: Changed validation to check for existence of award rather than the sponsor award id field within the award
  * aakers on Wed, 2 Aug 2017 12:52:33 -0600 [View Commit](../../commit/611dfce607c1be3fdaccc6b9ac12be3c6cbf1c7a)
* Updated CORE.md to include default values for parameters as found in kc-config-defaults.xml
  * Tyler Wilson on Fri, 2 Jun 2017 12:16:36 -0600 [View Commit](../../commit/64d5fa5a5f787c5549d056820c7b70994530936c)
* Updated CORE.md based on feedback
  * Tyler Wilson on Fri, 2 Jun 2017 09:00:48 -0600 [View Commit](../../commit/4f2ec94df40922790a6e0e12dd7b975bf76ec36b)
* Core Auth implementation in monolith
  * Tyler Wilson on Thu, 27 Apr 2017 16:30:23 -0600 [View Commit](../../commit/7ae397646e182427ce5f81160c1ef596a8e63fed)
* Core Auth implementation in monolith
  * Tyler Wilson on Thu, 27 Apr 2017 16:30:23 -0600 [View Commit](../../commit/15b165b46455effbc35ec84005748bdf960ba135)

## coeus-1802.0011
* No Changes


## coeus-1802.0010
* No Changes


## coeus-1802.0009
* RESKC-2733, RESKC-2734, RESKC-2735, RESKC-2737, RESKC-2738: fix contact mappings based on contact code
  * Travis Schneeberger on Tue, 6 Feb 2018 14:13:55 -0500 [View Commit](../../commit/8cd90b16f853f0f03f2b08b6bf54ec4b28386716)
* RESKC-1763, RESKC-2765: updating fdp forms attachment 3b and attachment 3b page2
  * Travis Schneeberger on Tue, 6 Feb 2018 11:58:01 -0500 [View Commit](../../commit/7fedb349369587ec7e5aa271d8cd738b23fcea75)
* RESKC-2729: Fix ambiguous column issue in mysql ART update script
  * Jeff Largent on Tue, 6 Feb 2018 11:41:25 -0500 [View Commit](../../commit/940512d4938a47708072dc671c7a21082a91826e)

## coeus-1802.0007
* RESKC-2678: Update award close dates on every save rather than just on the reports tab
  * Jeff Largent on Mon, 5 Feb 2018 15:38:41 -0500 [View Commit](../../commit/60f3740ddf57ca5fe6dc5d01ac4a86f64614919f)

## coeus-1802.0006
* No Changes


## coeus-1802.0005
* RESKC-2760: updating s2s.  updating other minor build dependencies
  * Travis Schneeberger on Mon, 5 Feb 2018 13:45:07 -0500 [View Commit](../../commit/d5e7277413a007a81f37b9ba84121a9044dc216a)

## coeus-1802.0004
* RESKC-2723: Move scripts to the correct folder
  * Travis Schneeberger on Mon, 5 Feb 2018 12:56:43 -0500 [View Commit](../../commit/9156abf0b0b762ccb5de5ea9efa75e7b122d24f6)
* Fix Oracle (#2736)

  * Gayathri Athreya on Mon, 5 Feb 2018 10:34:43 -0700 [View Commit](../../commit/a716aabc0e3c89c1933910045ddd70b18dc6e33b)
* Fix mysql (#2735)

  * Gayathri Athreya on Mon, 5 Feb 2018 10:04:50 -0700 [View Commit](../../commit/943f6fee4b9553358563f266a733abab0fb1944f)

## coeus-1802.0003
* RESKC-2762: NIH validation messages (#2731)

  * Gayathri Athreya on Mon, 5 Feb 2018 08:32:40 -0700 [View Commit](../../commit/4af984ce71936af919d999110cfba3ac53717c43)

## coeus-1802.0002
* RESKC-2729: Update award report tracking versioning script to disambiguate as required terms based on due date
  * Jeff Largent on Wed, 24 Jan 2018 14:34:32 -0500 [View Commit](../../commit/bfff4984eaa13b1651747cc2a4a0fe920103d427)
* RESKC-2763: Duplicate messages show up with caching enabled when using custom messages
  * Travis Schneeberger on Sat, 3 Feb 2018 08:14:57 -0500 [View Commit](../../commit/0b411c0b786db5dfd4608f10bf14d191b14d16e8)
* RESKC-2733, RESKC-2734, RESKC-2735, RESKC-2737, RESKC-2738: bug fixes, cleaning up unused elements in the fdp xml document.
  * Travis Schneeberger on Sat, 3 Feb 2018 08:13:10 -0500 [View Commit](../../commit/c5e2f9f159beb97fa028879c1ff0f3ce6ec4b6bf)
* RESKC-724: Allow updating existing report trackings even if report class...

  * ...is set to not generate reporting requirements. This way historical trackings can still be updated after that flag has been changed.
  * Jeff Largent on Fri, 26 Jan 2018 15:16:41 -0500 [View Commit](../../commit/5bbdb4768cf0f0f629fc7fd99af8c4ada38ae3a6)

## coeus-1802.0001
* RESKC-2761: Caching NIH Validation Service responses in order to avoid remote calls when S2S relevant data hasn't changed.
  * Travis Schneeberger on Wed, 31 Jan 2018 15:08:11 -0500 [View Commit](../../commit/3fe18adbf609891783c948a5d94477bc1c495c59)

## coeus-1801.0038
* RESKC-2645: Leave feedack link parameter blank to use current XML config value by default
  * Jeff Largent on Fri, 26 Jan 2018 14:52:12 -0500 [View Commit](../../commit/9a050fd82a120a2f77c6fcef1256d2fc7994f76b)

## coeus-1801.0037
* RESKC-2645: Updated PD and Budget help links to be parameter-based and point to Zendesk

* Also updated the portal "GET HELP" link to be parameterized as well
  * Jeff Largent on Thu, 25 Jan 2018 15:36:29 -0500 [View Commit](../../commit/e409b04739042d84fffd3f18c6050d5cc8f95e00)

## coeus-1801.0036
* No Changes


## coeus-1801.0035
* Reskc 2655 bilateral form (#2724)

* RESKC-2655, RESKC-2504: Subaward Modification Bilateral template.
  * 
* RESKC-2655: Remove old artifacts
  * Gayathri Athreya on Thu, 25 Jan 2018 11:34:46 -0700 [View Commit](../../commit/fd6a9ac5c275dc82e5cd298bc9ac81a084cedbef)
* RESKC-1215: MIT fix to only send ART notifications for pending trackings associated with active awards
  * Jeff Largent on Thu, 25 Jan 2018 13:17:57 -0500 [View Commit](../../commit/c1e14805356c34ddaea5f9000fb653c9187b41bc)

## coeus-1801.0033
* RESKC-2553: Apply day offsets to dates after schedule has been generated

* This allows offsets to be applied correctly to months with different numbers of days
  * Jeff Largent on Wed, 24 Jan 2018 11:33:34 -0500 [View Commit](../../commit/93dbd0b20da78f297646d338c0ebfdc932e8734d)
* RESKC-2536: create barebones DD entry for KcNotificationDocument
  * Travis Schneeberger on Wed, 24 Jan 2018 11:07:00 -0500 [View Commit](../../commit/5a7bd5c97b9ec01375da2d81b8df9db5bec8753e)
* RESKC-2270: Protect against situations where no valid entries have been set up for report class / report / frequencies
  * Jeff Largent on Fri, 12 Jan 2018 17:25:51 -0500 [View Commit](../../commit/2628363b3f3c35903b12fa80a5cde54bb5ea76d4)

## coeus-1801.0031
* No Changes


## coeus-1801.0030
* Change unitNumber to unit. (#2720)

  * Gayathri Athreya on Tue, 23 Jan 2018 10:52:53 -0700 [View Commit](../../commit/ef128a77fda252474ed3dd3bdcdfe9d7ed55ab9e)
* RESKC-2090: Don't add personnel summary totals twice
  * Jeff Largent on Tue, 23 Jan 2018 11:09:00 -0500 [View Commit](../../commit/49157a7c9a785dd51e4f7080edcc1712fd7f31fd)
* RESKC-2620:Fix tests (#2718)

  * Gayathri Athreya on Mon, 22 Jan 2018 15:36:17 -0700 [View Commit](../../commit/e8971a118900255af63fb33dfbf78f29bb574451)
* RESKC-2703: update copyright
  * Travis Schneeberger on Mon, 22 Jan 2018 11:21:12 -0500 [View Commit](../../commit/29ba91c708e60d23268d80c799a67789043188d5)
* RESKC-2719, RESKC-2651, RESKC-2654: updating fdp pdf forms
  * Travis Schneeberger on Mon, 22 Jan 2018 11:12:08 -0500 [View Commit](../../commit/d57da4803d97a950de2808893c28314a22c3b6ee)
* RESKC-2620: Add unit number to object codes table. (#2716)

* RESKC-2620: Add unit number to object codes table.
  * 
* PR review
  * Gayathri Athreya on Mon, 22 Jan 2018 14:16:34 -0700 [View Commit](../../commit/a0fb587e3958d22861295b0065de0c15173c9d7b)

## coeus-1801.0029
* No Changes


## coeus-1801.0028
* No Changes


## coeus-1801.0027
* No Changes


## coeus-1801.0026
* RESKC-2723: making attachments optional for Other Project Information form 1.3 and 1.4
  * Travis Schneeberger on Thu, 18 Jan 2018 11:54:54 -0500 [View Commit](../../commit/594d56170c040817be0000b096e0f855ca236cdf)

## coeus-1801.0025
* RESKC-2090: Revert fix (#2714)

  * Gayathri Athreya on Wed, 17 Jan 2018 12:30:09 -0700 [View Commit](../../commit/b55b3a1ac52d73f0e59129621b075deb201c4c51)

## coeus-1801.0024
* No Changes


## coeus-1801.0023
* Copyright intelli j (#2712)

* RESKC-2703: xml files
  * 
* RESKC-2703: Java and js files
  * 
* RESKC-2703: More files with different type of copyright
  * 
* RESKC-2703: Mild changes to right header and more java changes
  * 
* RESKC-2703: xsl and more xml
  * 
* RESKC-2703: Other miscellaneous comment types
  * 
* RESKC-2703: License update
  * 
* RESKC-2703: Changing license in pom
  * Gayathri Athreya on Tue, 16 Jan 2018 08:19:14 -0700 [View Commit](../../commit/34c042268acf79792d4d39d7a87aa942f000b06a)

## coeus-1801.0022
* RESKC-2653: Adding attachment 1 (#2708)

  * Gayathri Athreya on Fri, 12 Jan 2018 15:32:06 -0700 [View Commit](../../commit/e704eb7bf183b5904290831542eaaa1c5310eea3)

## coeus-1801.0021
* RESKC-2715: Fix ART notification integration test
  * Jeff Largent on Fri, 12 Jan 2018 16:27:44 -0500 [View Commit](../../commit/2b5d5a9ee5a6e332b8fb82de2841fb6316af6a7f)
* RESKC-2715: Base Award Report Tracking sent notifications records on report class and code rather than tying them to a specific term ID (#2709)

* Changed report tracking notifications to avoid sending duplicates based on report class and code rather than term ID
* Included maintenance screen to search and delete sent report notification records to allow them to be re-triggered if necessary
* Also refactored several commonly-used report tracking property names into their own constants class
  * Jeff Largent on Fri, 12 Jan 2018 15:17:36 -0500 [View Commit](../../commit/d90ef120b5aa2bf80b7bbc9671fc72371c472f04)

## coeus-1801.0020
* RESKC-2090: Don't add personnel summary totals twice
  * Jeff Largent on Wed, 10 Jan 2018 14:41:07 -0500 [View Commit](../../commit/487991668a1b10a742d4cf57386d6553af50a095)

## coeus-1801.0019
* RESKC-2717: Fix date format to show proper month (#2707)

  * Terry Durkin on Thu, 11 Jan 2018 14:07:31 -0500 [View Commit](../../commit/6ad5750b5f789e5513680128bd08d25afd63e50a)

## coeus-1801.0018
* No Changes


## coeus-1801.0017
* No Changes


## coeus-1801.0016
* RESKC-2716, RESKC-2471: show/hide t and c f coi.  Prevent STE when printing DOE fdp form.
  * Travis Schneeberger on Wed, 10 Jan 2018 16:25:57 -0500 [View Commit](../../commit/0f147b45502077287e6db12511e2ebd64cf6b98f)
* RESKC-2500: NSF CP form (#2704)

* RESKC-2500: NSF CP form
  * 
* RESKC-2500: small change
  * Gayathri Athreya on Wed, 10 Jan 2018 13:13:52 -0700 [View Commit](../../commit/a785c80489057c1c04e573ecc49b1d27246176c0)

## coeus-1801.0015
* RESKC-2624: create the correct sort order for fdp forms, cleanup unused stylesheets that have been replaced by pdf forms
  * Travis Schneeberger on Wed, 10 Jan 2018 13:56:42 -0500 [View Commit](../../commit/6d3991a63e071c3166dc27e08a5ed6fd99f43799)

## coeus-1801.0014
* RESKC-2132: Changed behavior back to only deleting pending reports when schedule changes

* Added support for manually deleting tracking entries which no longer match the schedule
* Made sure that report tracking entries are updated when report / frequency properties are changed on the associated term
  * Jeff Largent on Wed, 10 Jan 2018 11:23:56 -0500 [View Commit](../../commit/c350d1815d4447f70f1ccdd5e083187d2af20220)

## coeus-1801.0012
* RESKC-2652: increase field size in DD to match DB
  * Travis Schneeberger on Tue, 9 Jan 2018 17:42:51 -0500 [View Commit](../../commit/7da3ad3156d47db4c91b4133c20c9479cba4bfca)

## coeus-1801.0011
* RESKC-2688: Change long to varchar (#2699)

  * Gayathri Athreya on Tue, 9 Jan 2018 10:42:00 -0700 [View Commit](../../commit/ad1c4cdb8b7fd0113b8985d633eecaad593e4458)
* RESKC-2652: update fdp agreement
  * Travis Schneeberger on Tue, 9 Jan 2018 11:38:58 -0500 [View Commit](../../commit/91100a52b912aefbf332ad8f62db0ff91368d9d7)

## coeus-1801.0009
* RESKC-2642: zendesk-3192 check for null prime sponsor (#2626)

  * Noah on Mon, 8 Jan 2018 10:15:24 -0800 [View Commit](../../commit/a3b5319a8c75dc5805bdc439bb3fdc2dc107d8e5)

## coeus-1801.0008
* RESKC-869: Disabling award_id constraint until we can fix orphaned tracking records
  * Jeff Largent on Fri, 5 Jan 2018 17:34:46 -0500 [View Commit](../../commit/185b4bc6e635e62be725fc555abab4819618aa7e)

## coeus-1801.0007
* RESKC-869: Fix broken IT
  * Jeff Largent on Fri, 5 Jan 2018 15:11:36 -0500 [View Commit](../../commit/9dda734cdf1656d172f78062c9c1bc79c427540d)
* RESKC-1748: Unify award report tracking sync behavior

* No longer regenerate tracking entries with Pending status on every save
* Always regenerate schedules for both `REGEN` and `ADDONLY` frequency bases, preserving trackings with dates that match the new schedule
  * Jeff Largent on Fri, 5 Jan 2018 12:32:58 -0500 [View Commit](../../commit/3f7b06f7566b247f69cbebc696e59a2aac106bfd)
* RESKC-869: Store award ID with award report tracking entries

* Also updated report tracking lookup to only return entries associated with `ACTIVE` or `PENDING` awards
* Included script to re-map existing report tracking entries to the award terms associated with the latest award version
  * Jeff Largent on Fri, 5 Jan 2018 12:24:52 -0500 [View Commit](../../commit/37e771ee9b066fc541a2bb8c39952adbf05feecd)

## coeus-1801.0006
* RESKC-2688: Adding NSF ID field (#2693)

  * To support the upcoming NSF changes, add the new NSF ID field to the Extended Attributes (near the NIH ERA Commons ID)
  * Please add a display field for the NSF in the Key Personnel page, again near the ERA Commons field, and automatically fill this when the person is added.  * Gayathri Athreya on Thu, 4 Jan 2018 14:10:31 -0700 [View Commit](../../commit/e60064093b5eba98c60b402655856c959fb709c1)

## coeus-1801.0005
* RESKC-2694: implement workaround for User Attached Form: Human Studies Clinical Trial form with Human Studies attachment
  * Travis Schneeberger on Wed, 3 Jan 2018 16:47:24 -0500 [View Commit](../../commit/ba24f85dde9e2df2bb8f204a43c862cb3b3cccde)

## coeus-1801.0004
* RESKC-2232: Enabling notifications (#2692)

  * Steps to Reproduce
  * Create an award with basic information to submit/finalize
  * Add a property report with a due date in the past
submit award
  * Actual Result
  * No notification is sent out to the persons listed on the notification document.
  * Expected Result
  * The system should generate emails/action list items triggered off of the events specified in the notifications table. (Ex: Property report Overdue) It looks like the triggers are not in place for these to be sending.  * Gayathri Athreya on Thu, 4 Jan 2018 08:36:37 -0700 [View Commit](../../commit/e0785791544819a67a7f1334eed86ec94f3205ac)

## coeus-1801.0003
* RESKC-2467: Copy recipients list in tag file to avoid ConcurrentModificationException
  * Jeff Largent on Tue, 2 Jan 2018 15:53:55 -0500 [View Commit](../../commit/5c093231823f0f938c4e933bb3a66514671e3137)

## coeus-1801.0002
* RESKC-2293: Reducing length of field to match db (#2689)

  * Gayathri Athreya on Tue, 2 Jan 2018 10:57:03 -0700 [View Commit](../../commit/e9e7b824883cd1e0a7814cd7a10bcaa80a83dd4e)

## coeus-1801.0001
* RESKC-2290: Add filter to set caching headers on static resources
  * Jeff Largent on Tue, 2 Jan 2018 10:36:24 -0500 [View Commit](../../commit/7d9aba95b6e798244e8f21739d2cf2b91a12f4b1)