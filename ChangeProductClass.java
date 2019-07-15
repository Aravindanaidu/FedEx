package com.bsgi.capture;

import java.rmi.RemoteException;

import org.w3c.dom.Document;








import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.ue.YCPBeforeTaskCompletionUE;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.YFSException;

public class ChangeProductClass{
	public static YIFApi api;
	public ChangeProductClass() throws YIFClientCreationException {
		api = YIFClientFactory.getInstance().getApi();
	}	

	public void changeProductClass(YFSEnvironment env, Document inXML) throws RuntimeException, RemoteException{
		
		YFCDocument input = YFCDocument.getDocumentFor(inXML);
		System.out.println("---input xml"+input);
		YFCElement task = input.getDocumentElement();
		
		
		
		YFCDocument inDoc=YFCDocument.getDocumentFor("<Task TaskKey=\""+task.getAttribute("TaskKey") +"\"></Task>");
		YFCDocument tempDoc= YFCDocument.getDocumentFor("<Task EnterpriseKey=\"\" IsSummaryTask=\"\" Node=\"\" OrganizationCode=\"\" ParentTaskId=\"\" ReasonCode=\"\" SourceLocationId=\"\" SourceZoneId=\"\" TargetLocationId=\"\" TaskId=\"\" TaskKey=\"\" TaskType=\"\" >"
				+ "<Inventory InventoryStatus=\"\" ItemId=\"\" ProductClass=\"\" Quantity=\"\"  UnitOfMeasure=\"\">"
						+ "<TagAttributes BatchNo=\"\" LotAttribute1=\"\" LotAttribute2=\"\" LotAttribute3=\"\" LotKeyReference=\"\" LotNumber=\"\" ManufacturingDate=\"\" RevisionNo=\"\" TagNumber=\"\" />"
								+ "<Item ItemID=\"\" ItemKey=\"\" OrganizationCode=\"\" UnitOfMeasure=\"\">"
										+ "</Item>"
										+ "</Inventory>"
										+ "<TaskReferences />"
										+ "<TaskType ActivityCode=\"\" ActivityGroupId=\"\" >"
												+ "</TaskType>"
												+ "</Task>");
		
		env.setApiTemplate("getTaskDetails", tempDoc.getDocument());
		
		Document taskdetails = api.invoke(env, "getTaskDetails", inDoc.getDocument());
		env.clearApiTemplate("getTaskDetails");
		YFCDocument  tdet = YFCDocument.getDocumentFor(taskdetails);
		YFCElement rootelement = tdet.getDocumentElement();
		System.out.println("task header is----"+rootelement);
		String enterprisecode = rootelement.getAttribute("EnterpriseKey");
		String node = rootelement.getAttribute("Node");
		String locationid = rootelement.getAttribute("TargetLocationId");
		//String reasoncode = rootelement.getAttribute("ReasonCode");
		String reasoncode = "RECEIPT";
		String ignoreordering = "Y";
		System.out.println("Enterprise, node, locationId are---"+enterprisecode+"\t"+node+"\t"+locationid);
		
		
		YFCElement inventory = rootelement.getChildElement("Inventory");
		System.out.println("Inventory tag is---"+inventory);
		String itemId = inventory.getAttribute("ItemId");
		String inventoryStatus = inventory.getAttribute("InventoryStatus");
		String productclass = inventory.getAttribute("ProductClass");
		String uom = inventory.getAttribute("UnitOfMeasure");
		System.out.println("Itemid, Inventory Status, product class and UOM are---"+itemId+"\t"+inventoryStatus+"\t"+productclass+"\t"+uom);
		
		
		YFCDocument tempDoc2= YFCDocument.getDocumentFor("<ChangeLocationInventoryAttributes EnterpriseCode=\""+enterprisecode+"\" IgnoreOrdering=\""+ignoreordering+"\" Node=\""+node+"\" >"
				+ "<Source CaseId=\"\" LocationId=\""+locationid+"\" PalletId=\"\" >"
						+ "<FromInventory CaseIdQryType=\"VOID\" CountryOfOrigin=\"\" FifoNo=\"0\" InventoryStatus=\"N\" PalletIdQryType=\"VOID\" Quantity=\"\" Segment=\"\" SegmentType=\"\" ShipByDate=\"\" >"
								+ "<InventoryItem ItemID=\""+itemId+"\" ProductClass=\""+productclass+"\" UnitOfMeasure=\""+uom+"\" />"
										+ "</FromInventory>"
						+ "<ToInventory CountryOfOrigin=\"\" FifoNo=\"0\" InventoryStatus=\"N\" Segment=\"\" SegmentType=\"\" >"
										+ "<InventoryItem ItemID=\""+itemId+"\" ProductClass=\"FQ\" UnitOfMeasure=\""+uom+"\" />"
												+ "</ToInventory>"
										+ "</Source>"
										+ "<Audit ReasonCode=\""+reasoncode+"\" ReasonText=\"\" Reference1=\"\"  Reference2=\"\"  Reference3=\"\"  Reference4=\"\"  Reference5=\"\"  />"
												+ "</ChangeLocationInventoryAttributes>");
		
		env.setApiTemplate("changeLocationInventoryAttributes", tempDoc2.getDocument());
		
		api.invoke(env, "changeLocationInventoryAttributes", tempDoc2.getDocument());
		env.clearApiTemplate("changeLocationInventoryAttributes");
		

//		YFCElement task = input.getDocumentElement();
//        String assignedtouserid = task.getAttribute("AssignedToUserId");
//        System.out.println("----The user who completed task is----"+assignedtouserid);
//        
//        YFCElement inventory = task.getChildElement("Inventory");
//        System.out.println("Inventory tag is---"+inventory);
       // inventory.setAttribute("ProductClass","FQ");
        //System.out.println("--product class is--"+productclass);
       // task.setAttribute("Product Class", "FQ");
        
		
		
		
		
		
			
			//env.setApiTemplate("translateBarCode", tempDoc.getDocument());
		
		}

	}

