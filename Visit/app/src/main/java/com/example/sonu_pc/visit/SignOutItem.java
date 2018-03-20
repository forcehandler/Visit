package com.example.sonu_pc.visit;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;

/**
 * Created by sonupc on 16-03-2018.
 */

public class SignOutItem implements SortedListAdapter.ViewModel{

    public String id;
    public String name;
    public String signInTime;

    public SignOutItem(String id, String name, String signInTime) {
        this.id = id;
        this.name = name;
        this.signInTime = signInTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SignOutItem that = (SignOutItem) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return signInTime != null ? signInTime.equals(that.signInTime) : that.signInTime == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (signInTime != null ? signInTime.hashCode() : 0);
        return result;
    }
}
