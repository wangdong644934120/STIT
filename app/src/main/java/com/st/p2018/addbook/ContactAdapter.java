package com.st.p2018.addbook;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.st.p2018.stit.R;
import com.st.p2018.util.CacheSick;
import com.st.p2018.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<String> mContactNames; // 联系人名称字符串数组
    private List<String> mContactList; // 联系人名称List（转换成拼音）
    private List<Contact> resultList; // 最终结果（包含分组的字母）
    private List<String> characterList; // 字母List



    public enum ITEM_TYPE {
        ITEM_TYPE_CHARACTER,
        ITEM_TYPE_CONTACT
    }

    public ContactAdapter(Context context, List<String> contactNames) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mContactNames = contactNames;
        handleContact();
    }

    private void handleContact() {
        resultList = new ArrayList<>();
        for(int i=0;i<mContactNames.size();i++){
            resultList.add(new Contact(mContactNames.get(i), ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal()));
        }

        /*mContactList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < mContactNames.size(); i++) {

            String pinyin = Utils.getPingYin(mContactNames.get(i));

            map.put(pinyin, mContactNames.get(i));
            mContactList.add(pinyin);
        }

        Collections.sort(mContactList, new ContactComparator());

        resultList = new ArrayList<>();
        characterList = new ArrayList<>();

        for (int i = 0; i < mContactList.size(); i++) {
            String name = mContactList.get(i);
            String character = (name.charAt(0) + "").toUpperCase(Locale.ENGLISH);
            if (!characterList.contains(character)) {
                if (character.hashCode() >= "A".hashCode() && character.hashCode() <= "Z".hashCode()) { // 是字母
                    characterList.add(character);
                    resultList.add(new Contact(character, ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                } else {
                    if (!characterList.contains("#")) {
                        characterList.add("#");
                        resultList.add(new Contact("#", ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                    }
                }
            }

            resultList.add(new Contact(map.get(name), ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal()));
        }*/
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()) {
            return new CharacterHolder(mLayoutInflater.inflate(R.layout.item_character, parent, false));
        } else {
            return new ContactHolder(mLayoutInflater.inflate(R.layout.item_contact, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CharacterHolder) {
            ((CharacterHolder) holder).mTextView.setText(Html.fromHtml(resultList.get(position).getmName()));

        } else if (holder instanceof ContactHolder) {
            ((ContactHolder) holder).mTextView.setText(Html.fromHtml(resultList.get(position).getmName()));

        }
    }

    @Override
    public int getItemViewType(int position) {
        return resultList.get(position).getmType();
    }

    @Override
    public int getItemCount() {
       return resultList == null ? 0 : resultList.size();
    }

    public class CharacterHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        CharacterHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.character);
        }
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ContactHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.contact_name);

            LinearLayout ll = (LinearLayout)view.findViewById(R.id.txl);

            ll.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v,MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP){
                        //System.out.println(mTextView.getText());
                       CacheSick.sickChoose=mTextView.getText().toString();
                        v.setFocusableInTouchMode(true);

                    }

                    return false;

                }
            });


  /*         ll.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    System.out.println("b:"+mTextView.getText());
                }
            });*/
        }


    }

    public int getScrollPosition(String character) {
        if (characterList.contains(character)) {
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).getmName().equals(character)) {
                    return i;
                }
            }
        }

        return -1; // -1不会滑动
    }



}
