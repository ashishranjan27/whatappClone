package com.example.whatappfeature.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatappfeature.Models.MessageModel;
import com.example.whatappfeature.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatAdapter extends  RecyclerView.Adapter{

    ArrayList<MessageModel> messageModels;
    Context context;
    String recId;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;


    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String recId) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId = recId;
    }

    @Override
    public int getItemViewType(int position) {

        //means if the user in the messageModels is same as the user in FirebaseAuth.getInstance().getUid() (means same as the user which is login in the app)
        //then it means that he is sender one
        if(messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid()))
        {
            return SENDER_VIEW_TYPE;
        }
        else
        {
            return RECEIVER_VIEW_TYPE;
        }

        //return super.getItemViewType(position);    //given bydefault
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==SENDER_VIEW_TYPE){
            View view= LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewVolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sample_reciever,parent,false);
            return new RecieverViewVolder(view);
        }

        // return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
         MessageModel messageModel = messageModels.get(position);

         holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View v) {

                 new AlertDialog.Builder(context)
                         .setTitle("Delete")
                         .setMessage("Are you sure you want to delete the message")
                         .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 FirebaseDatabase database = FirebaseDatabase.getInstance();
                                 String senderRoom = FirebaseAuth.getInstance().getUid() + recId;

                                 //getting into the firebase and deleting the message from the sender side only when you delete the message from the app
                                 database.getReference().child("chats").child(senderRoom)
                                         .child(messageModel.getMessageId())
                                         .setValue(null);

                             }
                         }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                     }
                 }).show();

                 return false;
             }
         });

         //holder.getClass() comes from firebase
         if(holder.getClass() == SenderViewVolder.class){
             ((SenderViewVolder)holder).senderMsg.setText(messageModel.getMessage());
         }
         else
         {
             ((RecieverViewVolder)holder).receiverMsg.setText(messageModel.getMessage());
         }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class RecieverViewVolder extends RecyclerView.ViewHolder {

        TextView receiverMsg,receiverTime;
        public RecieverViewVolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.recieverText);
            receiverTime = itemView.findViewById(R.id.recieverTime);

        }
    }

    public class SenderViewVolder extends RecyclerView.ViewHolder {

        TextView senderMsg,senderTime;
        public SenderViewVolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);

        }
    }

}
