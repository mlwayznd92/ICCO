package com.miguelcatalan.materialsearchview.utils;

public class VnStringUtil {
	
	private static String[] ORIGINAL_CHAR = {
		"A", "Á", "À", "Ả", "Ạ", "Ã",
		"a", "á", "à", "ả", "ạ", "ã",
		"Ă", "Ắ", "Ằ", "Ẳ", "Ặ", "Ẵ",
		"ă", "ắ", "ằ", "ẳ", "ặ", "ẵ",
		"Â", "Ấ", "Ầ", "Ẩ", "Ậ", "Ẫ",
		"â", "ấ", "ầ", "ẩ", "ậ", "ẫ",
		"E", "É", "È", "Ẻ", "Ẹ", "Ẽ",
		"e", "é", "è", "ẻ", "ẹ", "ẽ",
		"Ê", "Ế", "Ề", "Ể", "Ệ", "Ễ",
		"ê", "ế", "ề", "ể", "ệ", "ễ",
		"O", "Ó", "Ò", "Ỏ", "Ọ", "Õ",
		"o", "ó", "ò", "ỏ", "ọ", "õ",
		"Ô", "Ố", "Ồ", "Ổ", "Ộ", "Ỗ",
		"ô", "ố", "ồ", "ổ", "ộ", "ỗ",
		"Ơ", "Ớ", "Ờ", "Ở", "Ợ", "Ỡ",
		"ơ", "ớ", "ờ", "ở", "ợ", "ỡ",
		"I", "Í", "Ì", "Ỉ", "Ị", "Ĩ",
		"i", "í", "ì", "ỉ", "ị", "ĩ",
		"Y", "Ý", "Ỳ", "Ỷ", "Ỵ", "Ỹ",
		"y", "ý", "ỳ", "ỷ", "ỵ", "ỹ",
		"U", "Ú", "Ù", "Ủ", "Ụ", "Ũ",
		"u", "ú", "ù", "ủ", "ụ", "ũ",
		"Ư", "Ứ", "Ừ", "Ử", "Ự", "Ữ",
		"ư", "ứ", "ừ", "ử", "ự", "ữ",
		"d", "đ","D","Đ"
	};
	
	private static String[] REPLACE_CHAR = {
		"A", "A", "A", "A", "A", "A",
		"a", "a", "a", "a", "a", "a",
		"A", "A", "A", "A", "A", "A",
		"a", "a", "a", "a", "a", "a",
		"A", "A", "A", "A", "A", "A",
		"a", "a", "a", "a", "a", "a",
		"E", "E", "E", "E", "E", "E",
		"e", "e", "e", "e", "e", "e",
		"E", "E", "E", "E", "E", "E",
		"e", "e", "e", "e", "e", "e",
		"O", "O", "O", "O", "O", "O",
		"o", "o", "o", "o", "o", "o",
		"O", "O", "O", "O", "O", "O",
		"o", "o", "o", "o", "o", "o",
		"O", "O", "O", "O", "O", "O",
		"o", "o", "o", "o", "o", "o",
		"I", "I", "I", "I", "I", "I",
		"i", "i", "i", "i", "i", "i",
		"Y", "Y", "Y", "Y", "Y", "Y",
		"y", "y", "y", "y", "y", "y",
		"U", "U", "U", "U", "U", "U",
		"u", "u", "u", "u", "u", "u",
		"U", "U", "U", "U", "U", "U",
		"u", "u", "u", "u", "u", "u",
		"d", "d","D","D"
	};
	
	public static String vnConvert(String orginalString) {
		
		if (orginalString == null){
			return null;
		} else {
			for (int i = 0; i < ORIGINAL_CHAR.length; i++) {
				orginalString = orginalString.replace(ORIGINAL_CHAR[i],
						REPLACE_CHAR[i]);
			}
			return orginalString;
		}
	}
}
