package com.ipph.bio.service;

import java.security.NoSuchAlgorithmException;

import com.hp.util.EMBLUtil;
import com.hp.util.MD5FileUtil;
import com.ipph.bio.model.BioPatent;
import com.ipph.bio.model.BioSequenceAC;
import com.ipph.bio.model.RecordFasta;

public class FASTAReader {
	private String basePath = "D:\\林林\\工作文件";

	/**
	 * 
	 * @param fasta
	 * @param bioSeq
	 *            >NRNL1:NRN_DJ206055 PN:WO2006024951 A2
	 */
	public void readFASTA1(RecordFasta fasta, BioSequenceAC bioSeq, BioPatent bioPatent) throws NoSuchAlgorithmException  {
		String document = fasta.Header;
		try {
			if (document.startsWith(">")) {
				document = document.replace(">", "");
				// MD5
				bioSeq.MD5 = fasta.MD5;
				// TODO:SeqID + "_" + AC
				bioSeq._id = fasta.MD5 + "_" + fasta.SeqID;
				// 原始记录号
				bioSeq.AC = fasta.SeqID;
				bioSeq.SeqID = fasta.SeqID;
				bioSeq.Seq = fasta.Seq;
				String[] arr = document.split(" ");
				if (arr.length > 0) {
					String[] arr1 = arr[0].split(":");
					// ID
					if (arr1.length > 1) {
						bioSeq.Source = arr1[0];
						bioPatent.PatID = bioSeq.AC;
					}
				}
				// 文献类型
				if (arr.length > 2){
					bioPatent.kind = arr[2];
				}
				// PN
				if (arr.length > 1) {
					String[] arr2 = arr[1].split(":");
					if (arr2.length > 1) {
						String pn = arr2[1];
						String country = pn.substring(0, 2);
						bioPatent.country = country;
						bioPatent.PNO = pn + bioPatent.kind;
						//System.out.println(country);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "D:\\林林\\工作文件\\DI\\国外生物序列\\nrn11_fasta.txt";
		path = "D:\\林林\\工作文件\\DI\\国外生物序列\\other_fasta.txt";
		FASTAReader reader = new FASTAReader();
		// reader.readFASTA1(path);
		System.out.println("END");
	}

}
