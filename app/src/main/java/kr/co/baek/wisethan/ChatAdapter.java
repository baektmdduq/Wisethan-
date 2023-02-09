package kr.co.baek.wisethan;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>{
    private List<ChatData> chatList;
    private String name;

    public ChatAdapter(List<ChatData> chatData, String name){
        chatList = chatData;
        this.name = name;
        this.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        // requires static value, it means need to keep the same value
        // even if the item position has been changed.
        //itemView.get(position).getId();
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public TextView msgText;
        public View rootView;
        public TextView leftMsgTimeText, rightMsgTimeText, leftImageTimeText, rightImageTimeText;
        public ImageView imageView;
        public LinearLayout msgLinear, msgTimeLinear, imageTimeLinear;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.nameText);
            msgText = itemView.findViewById(R.id.msgText);
            leftMsgTimeText = itemView.findViewById(R.id.leftMsgTimeText);
            rightMsgTimeText = itemView.findViewById(R.id.rightMsgTimeText);

            imageView = itemView.findViewById(R.id.imageView);
            leftImageTimeText = itemView.findViewById(R.id.leftImageTimeText);
            rightImageTimeText = itemView.findViewById(R.id.rightImageTimeText);

            msgLinear = itemView.findViewById(R.id.msgLinear);
            msgTimeLinear = itemView.findViewById(R.id.msgTimeLinear);
            imageTimeLinear = itemView.findViewById(R.id.imageTimeLinear);

            msgLinear = itemView.findViewById(R.id.msgLinear);
            rootView = itemView;

            itemView.setEnabled(true);
            itemView.setClickable(true);
        }
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,
                parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(linearLayout);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {
        ChatData chat = chatList.get(position);

        holder.nameText.setText(chat.getName());
        if((chat.getMsg() != null) && !(chat.getMsg().equals(""))){
            holder.msgText.setText(chat.getMsg());
            holder.leftMsgTimeText.setText(chat.getTime());
            holder.rightMsgTimeText.setText(chat.getTime());
        }else{
            holder.msgText.setVisibility(View.GONE);
            holder.leftMsgTimeText.setVisibility(View.GONE);
            holder.rightMsgTimeText.setVisibility(View.GONE);
        }

        if(chat.getImageResId() != null){
            byte[] b = binaryStringToByteArray(chat.getImageResId());
            ByteArrayInputStream is = new ByteArrayInputStream(b);
            Drawable reviewImage = Drawable.createFromStream(is, "reviewImage");
            holder.imageView.setImageDrawable(reviewImage);
            holder.leftImageTimeText.setText(chat.getTime());
            holder.rightImageTimeText.setText(chat.getTime());
        }else{
            holder.imageView.setVisibility(View.GONE);
            holder.leftImageTimeText.setVisibility(View.GONE);
            holder.rightImageTimeText.setVisibility(View.GONE);
        }

        if(chat.getName().equals(this.name)){
            holder.nameText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            holder.msgText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            holder.leftMsgTimeText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            holder.rightMsgTimeText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

            holder.leftImageTimeText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            holder.rightImageTimeText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

            if(holder.imageView != null){
                holder.imageView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            }
            holder.rightMsgTimeText.setVisibility(View.GONE);
            holder.rightImageTimeText.setVisibility(View.GONE);

            holder.msgLinear.setGravity(Gravity.END);
            holder.msgTimeLinear.setGravity(Gravity.END);
            holder.imageTimeLinear.setGravity(Gravity.END);

        } else {
            holder.nameText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            holder.msgText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            holder.leftMsgTimeText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            holder.rightMsgTimeText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            holder.leftImageTimeText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            holder.rightImageTimeText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            if(holder.imageView != null){
                holder.imageView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            }

            holder.leftMsgTimeText.setVisibility(View.GONE);
            holder.leftImageTimeText.setVisibility(View.GONE);

            holder.msgLinear.setGravity(Gravity.START);
            holder.msgTimeLinear.setGravity(Gravity.START);
            holder.imageTimeLinear.setGravity(Gravity.START);
        }
    }

    @Override
    public int getItemCount() {
        return chatList == null ? 0 : chatList.size();
    }

    public void addChat(ChatData chatData){
        chatList.add(chatData);
        notifyItemInserted(chatList.size()-1);
    }

    // 스트링을 바이너리 바이트 배열로
    public static byte[] binaryStringToByteArray(String s) {
        int count = s.length() / 8;
        byte[] b = new byte[count];
        for (int i = 1; i < count; ++i) {
            String t = s.substring((i - 1) * 8, i * 8);
            b[i - 1] = binaryStringToByte(t);
        }
        return b;
    }

    // 스트링을 바이너리 바이트로
    public static byte binaryStringToByte(String s) {
        byte ret = 0, total = 0;
        for (int i = 0; i < 8; ++i) {
            ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
            total = (byte) (ret | total);
        }
        return total;
    }
}
