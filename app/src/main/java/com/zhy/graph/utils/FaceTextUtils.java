package com.zhy.graph.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.zhy.graph.bean.FaceText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
* @ClassName: FaceTextUtils 
* @Description: 表情解析 
* @author 余卓 
* @date 2014年10月21日 上午11:18:49 
*
 */
public class FaceTextUtils {

	public static List<FaceText> faceTexts = new ArrayList<FaceText>();
	static {
		faceTexts.add(new FaceText("[ue01]"));
		faceTexts.add(new FaceText("[ue02]"));
		faceTexts.add(new FaceText("[ue03]"));
		faceTexts.add(new FaceText("[ue04]"));
		faceTexts.add(new FaceText("[ue05]"));
		faceTexts.add(new FaceText("[ue06]"));
		faceTexts.add(new FaceText("[ue07]"));
		faceTexts.add(new FaceText("[ue08]"));
		faceTexts.add(new FaceText("[ue09]"));
		faceTexts.add(new FaceText("[ue10]"));
		faceTexts.add(new FaceText("[ue11]"));
		faceTexts.add(new FaceText("[ue12]"));
		faceTexts.add(new FaceText("[ue13]"));
		faceTexts.add(new FaceText("[ue14]"));
		faceTexts.add(new FaceText("[ue15]"));
		faceTexts.add(new FaceText("[ue16]"));
		faceTexts.add(new FaceText("[ue17]"));
		faceTexts.add(new FaceText("[ue18]"));
		faceTexts.add(new FaceText("[ue19]"));
		faceTexts.add(new FaceText("[ue20]"));
		faceTexts.add(new FaceText("[ue21]"));
		faceTexts.add(new FaceText("[ue22]"));
		faceTexts.add(new FaceText("[ue23]"));
		faceTexts.add(new FaceText("[ue24]"));
		faceTexts.add(new FaceText("[ue25]"));
		faceTexts.add(new FaceText("[ue26]"));
		faceTexts.add(new FaceText("[ue27]"));
		faceTexts.add(new FaceText("[ue28]"));
		faceTexts.add(new FaceText("[ue29]"));
		faceTexts.add(new FaceText("[ue30]"));
		faceTexts.add(new FaceText("[ue31]"));
		faceTexts.add(new FaceText("[ue32]"));
		faceTexts.add(new FaceText("[ue33]"));
		faceTexts.add(new FaceText("[ue34]"));
		faceTexts.add(new FaceText("[ue35]"));
		faceTexts.add(new FaceText("[ue36]"));
		faceTexts.add(new FaceText("[ue37]"));
		faceTexts.add(new FaceText("[ue38]"));
		faceTexts.add(new FaceText("[ue39]"));
		faceTexts.add(new FaceText("[ue40]"));
		faceTexts.add(new FaceText("[ue41]"));
		faceTexts.add(new FaceText("[ue42]"));
		faceTexts.add(new FaceText("[ue43]"));
		faceTexts.add(new FaceText("[ue44]"));
		faceTexts.add(new FaceText("[ue45]"));
		faceTexts.add(new FaceText("[ue46]"));
		faceTexts.add(new FaceText("[ue47]"));
		faceTexts.add(new FaceText("[ue48]"));
		faceTexts.add(new FaceText("[ue49]"));
		faceTexts.add(new FaceText("[ue50]"));
		faceTexts.add(new FaceText("[ue51]"));
		faceTexts.add(new FaceText("[ue52]"));
		faceTexts.add(new FaceText("[ue53]"));
		faceTexts.add(new FaceText("[ue54]"));
		faceTexts.add(new FaceText("[ue55]"));
		faceTexts.add(new FaceText("[ue56]"));
		faceTexts.add(new FaceText("[ue57]"));
		faceTexts.add(new FaceText("[ue58]"));
		faceTexts.add(new FaceText("[ue59]"));
		faceTexts.add(new FaceText("[ue60]"));
		faceTexts.add(new FaceText("[ue61]"));
		faceTexts.add(new FaceText("[ue62]"));
		faceTexts.add(new FaceText("[ue63]"));
		faceTexts.add(new FaceText("[ue64]"));
		faceTexts.add(new FaceText("[ue65]"));
		faceTexts.add(new FaceText("[ue66]"));
		faceTexts.add(new FaceText("[ue67]"));
		faceTexts.add(new FaceText("[ue68]"));
		faceTexts.add(new FaceText("[ue69]"));
		faceTexts.add(new FaceText("[ue70]"));
		faceTexts.add(new FaceText("[ue71]"));
		faceTexts.add(new FaceText("[ue72]"));
		faceTexts.add(new FaceText("[ue73]"));
		faceTexts.add(new FaceText("[ue74]"));
		faceTexts.add(new FaceText("[ue75]"));
		faceTexts.add(new FaceText("[ue76]"));
		faceTexts.add(new FaceText("[ue77]"));
		faceTexts.add(new FaceText("[ue78]"));
		faceTexts.add(new FaceText("[ue79]"));
		faceTexts.add(new FaceText("[ue80]"));
		faceTexts.add(new FaceText("[ue81]"));
		faceTexts.add(new FaceText("[ue82]"));
		faceTexts.add(new FaceText("[ue83]"));
		faceTexts.add(new FaceText("[ue84]"));
		faceTexts.add(new FaceText("[ue85]"));
		faceTexts.add(new FaceText("[ue86]"));
		faceTexts.add(new FaceText("[ue87]"));
		faceTexts.add(new FaceText("[ue88]"));
		faceTexts.add(new FaceText("[ue89]"));
		faceTexts.add(new FaceText("[ue90]"));
		faceTexts.add(new FaceText("[ue91]"));
		faceTexts.add(new FaceText("[ue92]"));
		faceTexts.add(new FaceText("[ue93]"));
		faceTexts.add(new FaceText("[ue94]"));
		faceTexts.add(new FaceText("[ue95]"));
	}

	public static String parse(String s) {
		for (FaceText faceText : faceTexts) {
			s = s.replace("\\" + faceText.text, faceText.text);
			s = s.replace(faceText.text, "\\" + faceText.text);
		}
		return s;
	}

	/** 
	  * toSpannableString
	  * @return SpannableString
	  * @throws
	  */
	public static SpannableString toSpannableString(Context context, String text) {
		if (!TextUtils.isEmpty(text)) {
			SpannableString spannableString = new SpannableString(text);
			int start = 0;
			Pattern pattern = Pattern.compile("\\[ue[0-9]{2}\\]", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				String faceText = matcher.group();
				String key = faceText.substring(1,faceText.length()-1);
				BitmapFactory.Options options = new BitmapFactory.Options();
				Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
						context.getResources().getIdentifier(key, "drawable", context.getPackageName()), options);
				ImageSpan imageSpan = new ImageSpan(context, bitmap);
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0)
					spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				start = (endIndex - 1);
			}

			return spannableString;
		} else {
			return new SpannableString("");
		}
	}

	public static SpannableString toSpannableString(Context context, String text, SpannableString spannableString) {
		String content = text;
		int start = 0;
		Pattern pattern = Pattern.compile("\\[ue[0-9]{2}\\]", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			content = spannableString.toString();
			String faceText = matcher.group();
			String key = faceText.substring(1,faceText.length()-1);
			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inSampleSize = 2;
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources()
					.getIdentifier(key, "drawable", context.getPackageName()), options);
			ImageSpan imageSpan = new ImageSpan(context, bitmap);
			int startIndex = content.indexOf(faceText, start);
			int endIndex = startIndex + faceText.length();
			if (startIndex >= 0)
				spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			start = (endIndex - 1);
		}

		return spannableString;
	}

}
