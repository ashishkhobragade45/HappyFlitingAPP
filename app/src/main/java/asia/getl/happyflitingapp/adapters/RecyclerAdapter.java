package asia.getl.happyflitingapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import asia.getl.happyflitingapp.R;
import asia.getl.happyflitingapp.model.FlightModel;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHoder> { List<FlightModel> list;
    Context context;
    String id,contact;

    public RecyclerAdapter(List<FlightModel> list, Context context, String str_id) {
        this.list = list;
        this.context = context;
        this.id = str_id;
    }

    @Override
    public MyHoder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        MyHoder myHoder = new MyHoder(view);


        return myHoder;
    }

    @Override
    public void onBindViewHolder(MyHoder holder, final int position) {
        FlightModel mylist = list.get(position);
        holder.name.setText("Flight Number : "+mylist.getFlightno());
        holder.cost.setText("Fair : "+mylist.getCost());
        holder.seat.setText("Seats Availability : "+mylist.getSeats());
        holder.denature.setText("Time : "+mylist.getTimed()+" - "+mylist.getTimea());

        holder.btn_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Click "+position, Toast.LENGTH_SHORT).show();


                // Create custom dialog object
                final Dialog dialog = new Dialog(context);
                // Include dialog.xml file
                dialog.setContentView(R.layout.my_dialog);
                // Set dialog title
                dialog.setTitle("Confirmed");

                // set values for custom dialog components - text, image and button
               final TextView book_title,book_time,book_seats,book_cost;
                final EditText edt_name;
               Button btn_confirmed,btn_cancel;
               book_title = (TextView)dialog.findViewById(R.id.book_title);
                edt_name = (EditText) dialog.findViewById(R.id.edt_name);
                book_seats = (TextView)dialog.findViewById(R.id.book_seats);
                book_cost = (TextView)dialog.findViewById(R.id.book_cost);
                book_time = (TextView)dialog.findViewById(R.id.book_time);

                final FlightModel mylist_book = list.get(position);

                book_title.setText("Flight Number : "+mylist_book.getFlightno());
                book_cost.setText("Total Fair : "+mylist_book.getCost());
                book_time.setText("Time : "+mylist_book.getTimed()+" - "+mylist_book.getTimea());

                dialog.show();

                btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                btn_confirmed = (Button) dialog.findViewById(R.id.btn_confirmed);
                // if decline button is clicked, close the custom dialog
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close dialog
                        dialog.dismiss();
                    }
                });

                btn_confirmed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name = edt_name.getText().toString().trim();

                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(context, "Enter Passenger Name!", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("userinfo");
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                {
                                    Log.e("testuser",dataSnapshot1.getKey());
                                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren())
                                    {

                                        if (Objects.equals(dataSnapshot2.getValue(), id))
                                        {
                                            String number = dataSnapshot1.getKey();


                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference myRef = database.getReference("bookingdb").child(Objects.requireNonNull(number))
                                                    .child(mylist_book.getFlightno()+"-"+mylist_book.getCost()+"-"+edt_name.getText().toString());
                                            myRef.child("flightno").setValue(mylist_book.getFlightno());
                                            myRef.child("cost").setValue(mylist_book.getCost());
                                            myRef.child("name").setValue(edt_name.getText().toString());

                                            setNumber(number);
                                            dialog.dismiss();

                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }
                });

            }
        });
    }

    private void setNumber(String number) {

        this.contact=number;

        Toast.makeText(context, "Booking confirmed...!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try{
            if(list.size()==0){

                arr = 0;

            }
            else{

                arr=list.size();
            }



        }catch (Exception e){



        }

        return arr;

    }

    class MyHoder extends RecyclerView.ViewHolder{
        TextView name,cost,seat,denature;
        Button btn_book;


        public MyHoder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.list_title);
            cost= (TextView) itemView.findViewById(R.id.list_desc);
            seat= (TextView) itemView.findViewById(R.id.list_seats);
            denature= (TextView) itemView.findViewById(R.id.list_denature);
            btn_book = (Button)itemView.findViewById(R.id.btn_book);

        }
    }

}