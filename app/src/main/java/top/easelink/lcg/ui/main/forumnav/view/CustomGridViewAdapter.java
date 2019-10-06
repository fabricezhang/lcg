package top.easelink.lcg.ui.main.forumnav.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import java.util.List;

import top.easelink.lcg.R;
import top.easelink.lcg.ui.main.model.ForumNavigationModel;

/**
 * author : junzhang
 * date   : 2019-07-28 23:06
 * desc   :
 */
public class CustomGridViewAdapter extends ArrayAdapter<ForumNavigationModel> {

    @LayoutRes
    private int mLayoutRes;

    CustomGridViewAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        mLayoutRes = resource;
    }

    @BindingAdapter({"adapter"})
    public static void addForumNavigationList(GridView gridView, List<ForumNavigationModel> navigationModelList) {
        CustomGridViewAdapter adapter = (CustomGridViewAdapter) gridView.getAdapter();
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(navigationModelList);
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
            holder.textView = convertView.findViewById(R.id.grid_text);
            holder.imageView = convertView.findViewById(R.id.grid_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ForumNavigationModel item = getItem(position);
        if (item == null) {
            // suppress the npe warning
            item = new ForumNavigationModel("PlaceHolder", R.drawable.launch_icon, "");
        }
        holder.textView.setText(item.getTitle());
        holder.imageView.setImageResource(item.getDrawableRes());
        final ForumNavigationModel model = item;
        return convertView;
    }

    private class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
