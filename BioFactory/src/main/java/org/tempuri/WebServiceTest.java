package org.tempuri;

import java.util.List;


public class WebServiceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DocdbnumService service = new DocdbnumService();
		DocdbnumServiceSoap soap = service.getDocdbnumServiceSoap();
		String country = "EP";
		String docnum = "1903115";
		String kind = "A1";
		//DocResult result = soap.getStdPublicationInfo("WO", "2012103865", "A2");
		DocResult result = soap.getStdPublicationInfo(country, docnum, kind);
		DocResult result2 = soap.getStdAppPubInfo(country, docnum, kind, 3,false);
		
		ArrayOfStdInfo array = result.getStdInfos();
		List<StdInfo> list = array.getStdInfo();
		for (StdInfo std : list){
			System.out.println(std.getSTDPUBCOUNTRY());
			System.out.println(std.getSTDPUBNUM());
			System.out.println(std.getSTDPUBDATE());
		}
	}

}
