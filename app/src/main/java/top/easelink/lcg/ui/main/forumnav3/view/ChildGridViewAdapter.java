package top.easelink.lcg.ui.main.forumnav3.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.List;

import top.easelink.lcg.R;
import top.easelink.lcg.ui.main.forumnav3.model.ChildForumItemInfo;

public class ChildGridViewAdapter extends ArrayAdapter<ChildForumItemInfo> {

    @LayoutRes
    private int mLayoutRes;

    ChildGridViewAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        mLayoutRes = resource;
    }

    public static void addForumList(GridView gridView, List<ChildForumItemInfo> childForumItemInfos) {
        ChildGridViewAdapter adapter = (ChildGridViewAdapter) gridView.getAdapter();
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(childForumItemInfos);
        }
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
            convertView = inflater.inflate(mLayoutRes, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.grid_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ChildForumItemInfo item = getItem(position);
        if (item == null) {
            // suppress the npe warning
            item = new ChildForumItemInfo(
                    "",
                    null,
                    "PlaceHolder",
                    null);
        }
        holder.textView.setText(item.getTitle());
        return convertView;
    }

    private class ViewHolder {
        TextView textView;
    }
}
