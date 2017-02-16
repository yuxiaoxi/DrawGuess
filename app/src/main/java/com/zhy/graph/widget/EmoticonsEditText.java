package com.zhy.graph.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
* @ClassName: EmoticonsEditText 
* @Description: 表情的textview
* @author 余卓 
* @date 2014年10月21日 上午11:19:10 
*
 */
public class EmoticonsEditText extends EditText implements OnKeyListener{

	public EmoticonsEditText(Context context) {
		super(context);
		setOnKeyListener(this);
	}

	public EmoticonsEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOnKeyListener(this);
	}

	public EmoticonsEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnKeyListener(this);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		if (!TextUtils.isEmpty(text)) {
			super.setText(replaceAt(replace(text.toString())), type);
		} else {
			super.setText(text, type);
		}
	}
	
	private Pattern buildPattern() {
		return Pattern.compile("\\[ue[0-9]{2}\\]", Pattern.CASE_INSENSITIVE);
	}
	private Pattern buildPatternAt() {
		return Pattern.compile("@[^\\s:：]+[:：\\s]", Pattern.CASE_INSENSITIVE);
	}
	

	private CharSequence replace(String text) {
		try {
			SpannableString spannableString = new SpannableString(text);
			int start = 0;
			Pattern pattern = buildPattern();
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				String faceText = matcher.group();
				String key = faceText.substring(1,faceText.length()-1);
				BitmapFactory.Options options = new BitmapFactory.Options();
				Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),
						getContext().getResources().getIdentifier(key, "drawable", getContext().getPackageName()), options);
				ImageSpan imageSpan = new ImageSpan(getContext(), bitmap);
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0)
					spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				start = (endIndex - 1);
			}
			return spannableString;
		} catch (Exception e) {
			return text;
		}
	}
	
	private CharSequence replaceAt(CharSequence charSequence) {
		try {
			SpannableString spannableString = new SpannableString(charSequence);
			String text  = spannableString.toString();
			int start = 0;
			Pattern pattern = buildPatternAt();
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				String atText = matcher.group();
				int startIndex = text.indexOf(atText, start);
				int endIndex = startIndex + atText.length();
				if (startIndex >= 0)
					spannableString.setSpan(new ForegroundColorSpan(0xff0077ff), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				start = (endIndex - 1);
			}
			return spannableString;
		} catch (Exception e) {
			return charSequence;
		}
	}

    @Override
    public boolean onKey(View arg0, int keyCode, KeyEvent arg2)
    {
        if(keyCode == KeyEvent.KEYCODE_ENTER){
            return true;
        }
        return false;
    }
	

	
}
