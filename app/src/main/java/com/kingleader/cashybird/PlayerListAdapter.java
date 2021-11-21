package com.kingleader.cashybird;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

class PlayerListAdapter extends ArrayAdapter<Player>
{

    Context mContext;
    int mResourceId;
    int lastposition = -1;
    static class ViewHolder {
        TextView serialNumber;
        TextView userName;
        TextView score;
        TextView checkOut;

    }

    public PlayerListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Player> objects) {
        super(context, resource, objects);
        mContext = context;
        mResourceId= resource;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

      //get the player information
      int serialNumber =getItem(position).getSerialNumber();
      String userName = getItem(position).getUserName();
      int score = getItem(position).getScore();
      int checkOut = getItem(position).getCheckOut();

      //create the player object with the information
        Player player=new Player(serialNumber,userName,score,checkOut);

        // new view
        final View result;
        ViewHolder holder;

        if(convertView ==null)
        {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResourceId,parent,false);
            holder = new ViewHolder();
            holder.serialNumber = convertView.findViewById(R.id.serialNumber);
            holder.userName = convertView.findViewById(R.id.userName);
            holder.score = convertView.findViewById(R.id.score);
            holder.checkOut = convertView.findViewById(R.id.checkOut);

            result = convertView;
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        Animation animation= AnimationUtils.loadAnimation(mContext,
                (position>lastposition) ? R.anim.load_down_anim: R.anim.load_up_anim);

        result.startAnimation(animation);
        lastposition=position;


        holder.serialNumber.setText(Integer.toString(serialNumber));
        holder.userName.setText(userName);
        holder.score.setText(Float.toString(score));
        holder.checkOut.setText(checkOut +"$");

        return  convertView;
    }
}
