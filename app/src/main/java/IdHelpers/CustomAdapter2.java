package IdHelpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bca.deepthinker.bca.R;

import java.util.ArrayList;


/**
 * Created by Ahmad on 1/10/2018.
 */

public class CustomAdapter2 extends BaseAdapter implements ListAdapter {


    private ArrayList<String> name = new ArrayList<String>();
    private Context context;






    public CustomAdapter2(ArrayList<String> name,Context context) {
        this.name = name;

        this.context = (Context) context;


    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int pos) {
        return name.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.allchatlists, null);
        }
        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string1);
        listItemText.setText(name.get(position));




        return view;
    }


}
