package com.izhiliao.util;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelUtil {

	/**
	 * 创建sheet的列名
	 * 
	 * @Description: TODO::
	 * @return void:
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	public static void createSheetColumn(WritableSheet sheet, int c, int r, String cont) throws RowsExceededException,
			WriteException {
		Label label = new Label(c, r, cont);
		sheet.addCell(label);
	}
}
