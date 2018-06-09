package in.bizzmark.smartpoints_user.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import in.bizzmark.smartpoints_user.NavigationActivity;
import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.bo.NavigationItems;

/**
 * Created by Admin on 8/2/2017.
 */

public class NavigationItemAdapter  extends ArrayAdapter<NavigationItems> {
    NavigationActivity navigationActivity;
    ArrayList<NavigationItems> navItemList;

    public NavigationItemAdapter(NavigationActivity navigationActivity, int resource, ArrayList<NavigationItems> navItemList) {
        super(navigationActivity, resource, navItemList);
        this.navigationActivity = navigationActivity;
        this.navItemList = navItemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) navigationActivity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.nav_items, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(getItem(position).getNavItemName());
        holder.icon.setImageResource(getItem(position).getNavItemImage());

        //handling each item on click
        //update main layout by this action
        convertView.setOnClickListener(onClickListener(position));
        return convertView;
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationActivity.updateMainLayout(getItem(position));
            }
        };
    }

    private class ViewHolder {
        private ImageView
                icon;
        private TextView name;

        public ViewHolder(View v) {
            icon = (ImageView) v.findViewById(R.id.nav_item_icon);
            name = (TextView) v.findViewById(R.id.nav_item_name);
        }
    }
}

