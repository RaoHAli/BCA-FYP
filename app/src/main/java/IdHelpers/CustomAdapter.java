package IdHelpers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;



import com.bca.deepthinker.bca.R;
import com.bca.deepthinker.bca.UserProfile;
import com.bca.deepthinker.bca.success;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;


import java.util.ArrayList;



/**
 * Created by Ahmad on 1/10/2018.
 */

public class CustomAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> Rating=new ArrayList<String>();
    private ArrayList<String> name = new ArrayList<String>();
    private ArrayList<Integer> count = new ArrayList<Integer>();
    private ArrayList<byte[]> pic = new ArrayList<byte[]>();
    private Context context;






    public CustomAdapter(ArrayList<String> name, ArrayList<Integer> count, ArrayList<String> Rating,ArrayList<byte[]> pic, Context context) {
        this.name = name;
        this.count = count;
        this.Rating=Rating;
        this.pic = pic;
        this.context = (Context) context;


    }

    @Override
    public int getCount() {
        return pic.size();
    }

    @Override
    public Object getItem(int pos) {
        return pic.get(pos);
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
            view = inflater.inflate(R.layout.allmessagelists, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        ImageView mesimg=view.findViewById(R.id.imageViewformes);
        TextView mescont=view.findViewById(R.id.textViewmescont);
        TextView mesrating=view.findViewById(R.id.textViewmescont2);



        listItemText.setText(name.get(position));
        if (Rating.size()>0) {
            mesrating.setText("Rating:"+Rating.get(position));
        }
      mescont.setText(count.get(position)+"");
      Bitmap bmp = BitmapFactory.decodeByteArray(pic.get(position), 0, pic.get(position).length);
        mesimg.setImageBitmap(Bitmap.createScaledBitmap(bmp, 50,
                50, false));



        mesimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              IdHelper.rating=true;
                Intent i=new Intent(context, UserProfile.class);
                context.startActivity(i);
            }
        });



        return view;
    }


}
