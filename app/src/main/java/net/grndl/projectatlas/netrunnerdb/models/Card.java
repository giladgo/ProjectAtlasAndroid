package net.grndl.projectatlas.netrunnerdb.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by giladgo on 11/21/14.
 */
public class Card implements Parcelable {
    public String code;
    public String title;
    public String type;
    public String subtype;
    public String faction;
    public String factionCode;
    public String imageUrl;
    public String setCode;
    public String sideCode;
    public Uri url;
    public int influence;
    public List<Card> recommendations;

    private static final Map<String, String> mFactionToSymbol = initFactionSymbolTable();

    public boolean isReal() {
        return !this.setCode.equals("special") && !this.setCode.equals("alt");
    }

    private static  Map<String, String> initFactionSymbolTable() {
        HashMap<String, String> factionToSymbol = new HashMap<>();
        factionToSymbol.put("shaper",             "\ue613");
        factionToSymbol.put("anarch",             "\ue605");
        factionToSymbol.put("criminal",           "\ue612");
        factionToSymbol.put("haas-bioroid",       "\ue602");
        factionToSymbol.put("weyland-consortium", "\ue607");
        factionToSymbol.put("jinteki",            "\ue609");
        factionToSymbol.put("nbn",                "\ue60b");
        return factionToSymbol;
    }

    public String getSymbol() {
        if (!factionCode.equals("neutral")) {
            return mFactionToSymbol.get(factionCode);
        } else {
            if (sideCode.equals("runner")) {
                return "\ue601";
            } else if (sideCode.equals("corp")) {
                return "\ue60d";
            }
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
    }


    public static final Parcelable.Creator<Card> CREATOR
            = new Parcelable.Creator<Card>() {
        public Card createFromParcel(Parcel in) {
            return CardDB.getInstance().getCard(in.readString());
        }

        public Card[] newArray(int size) {
            return new Card[size];
        }
    };
}
