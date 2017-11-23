package com.up.set.drivecell.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.up.set.drivecell.R;
import com.up.set.drivecell.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by amush on 11-Nov-17.
 */

public class PlacesFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "PlacesFragment";

    private RecyclerView eventsList;
    private DatabaseReference mDatabase;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_places, container, false);

        mDatabase= FirebaseDatabase.getInstance().getReference("Events");

        getActivity().setTitle("Places");

        initViews(view);
        Log.d(TAG, "onCreateView: Details");

        eventsList.hasFixedSize();
        eventsList.setLayoutManager(new LinearLayoutManager(getActivity()));



        return view;
    }

    private void initViews(View view) {
        eventsList=(RecyclerView)view.findViewById(R.id.eventsList);

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Event,EventsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Event, EventsViewHolder>(
                Event.class,
                R.layout.event_item,
                EventsViewHolder.class,
                mDatabase.orderByKey()

        ) {
            @Override
            protected void populateViewHolder(EventsViewHolder viewHolder, Event model, int position) {
                Log.d(TAG, "ModelView " + model.getEventName() + model.getEventPostTime() + model.getEventPostDate());
                Log.d(TAG, "Post Date: " + model.getEventPostDate());
                viewHolder.setEventPostDate(model.getEventPostDate());

                viewHolder.setEventPostTime(model.getEventPostTime());
                viewHolder.setEventsName(model.getEventName());
                viewHolder.setEventType(model.getEventType());
            }
        };

        eventsList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class EventsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public EventsViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
        }
        public void setEventsName(String name)
        {
            TextView eventName=(TextView)mView.findViewById(R.id.eventName);
            eventName.setText(name);
        }
        public void setEventPostDate(String date)
        {
            TextView eventDate=(TextView)mView.findViewById(R.id.eventDate);

            try {

                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
                Date oldDate =sdf.parse(date);
                Log.d(TAG, "Date "+sdf.format(oldDate));

                SimpleDateFormat sdf1= new SimpleDateFormat("EEE MMM dd");
                Date newDate =sdf1.parse(oldDate.toString());

                eventDate.setText(sdf1.format(newDate).toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }



        }
        public void setEventPostTime(String time) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");


            try {
                Date date = dateFormat.parse(time);
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("hh:mm aa");
                String newTime=dateFormat1.format(date);
                if(newTime.contains("0"))
                {
                    newTime=newTime.replace("0","");
                    TextView eventTime = (TextView) mView.findViewById(R.id.eventTime);
                    eventTime.setText(newTime);
                }
                else
                {
                    TextView eventTime = (TextView) mView.findViewById(R.id.eventTime);
                    eventTime.setText(newTime);
                }





            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        public void setEventType(String type)
        {
            TextView eventType=(TextView)mView.findViewById(R.id.eventType);

            if(type.equals("Fire"))
            {
                eventType.setTextColor(Color.parseColor("#fd151b"));
                eventType.setText(type);
            }
            else if(type.equals("Accident"))
            {
                eventType.setTextColor(Color.parseColor("#ed254e"));
                eventType.setText(type);
            }
            else if(type.equals("Road Block"))
            {
                eventType.setTextColor(Color.parseColor("#0039b3"));
                eventType.setText(type);
            }
            else if(type.equals("Closed Road"))
            {
                eventType.setTextColor(Color.parseColor("#465362"));
                eventType.setText(type);
            }
            else if(type.equals("Robbery"))
            {
                eventType.setTextColor(Color.parseColor("#011936"));
                eventType.setText(type);
            }
            else if(type.equals("Theft"))
            {
                eventType.setTextColor(Color.parseColor("#e89b29"));
                eventType.setText(type);
            }

        }

    }
}
