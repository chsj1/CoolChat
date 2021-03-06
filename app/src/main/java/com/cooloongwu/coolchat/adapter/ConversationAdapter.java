package com.cooloongwu.coolchat.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cooloongwu.coolchat.R;
import com.cooloongwu.coolchat.activity.ChatActivity;
import com.cooloongwu.coolchat.utils.GreenDAOUtils;
import com.cooloongwu.coolchat.entity.Conversation;
import com.cooloongwu.emoji.utils.EmojiTextUtils;
import com.cooloongwu.greendao.gen.ConversationDao;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 聊天会话列表页的适配器
 * Created by CooLoongWu on 2016-9-12 15:39.
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Conversation> listData;
    private LayoutInflater layoutInflater;

    public ConversationAdapter(Context context, ArrayList<Conversation> listData) {
        this.context = context;
        this.listData = listData;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(listData.get(position).getName());
        if ("image".equals(listData.get(position).getContentType())) {
            holder.content.setText("[图片]");
        } else if ("audio".equals(listData.get(position).getContentType())) {
            holder.content.setText("[语音]");
        } else if ("video".equals(listData.get(position).getContentType())) {
            holder.content.setText("[视频]");
        } else {
            //正则匹配下，如果有表情则显示表情
            holder.content.setText(EmojiTextUtils.getEditTextContent(
                    listData.get(position).getContent(),
                    context,
                    holder.content)
            );
        }
        holder.time.setText(listData.get(position).getTime().substring(11, 16));
        int unRead = listData.get(position).getUnReadNum();
        if (unRead > 0) {
            holder.num.setText(String.valueOf(unRead));
            holder.num.setVisibility(View.VISIBLE);
        } else {
            holder.num.setVisibility(View.GONE);
        }

        if (!listData.get(position).getAvatar().isEmpty()) {
            Picasso.with(context)
                    .load(listData.get(position).getAvatar())
                    .into(holder.avatar);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(context, ChatActivity.class);
                intent.putExtra("chatName", listData.get(position).getName());
                intent.putExtra("chatType", listData.get(position).getType());  //朋友还是群组
                intent.putExtra("chatId", listData.get(position).getMultiId());
                context.startActivity(intent);

                //将未读消息数置为0
                updateConversationDB(listData.get(position).getId());
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setNeutralButton("删除这条对话", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //如果确定那么删除该数据
                                Conversation conversation = new Conversation();
                                conversation.setId(listData.get(position).getId());
                                EventBus.getDefault().post(conversation);
                                //关闭对话
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
                //return true 后就不会再触发setOnClickListener事件
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView name;
        private TextView content;
        private TextView time;
        private TextView num;

        ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.conversation_avatar);
            name = (TextView) itemView.findViewById(R.id.conversation_name);
            content = (TextView) itemView.findViewById(R.id.conversation_content);
            time = (TextView) itemView.findViewById(R.id.conversation_time);
            num = (TextView) itemView.findViewById(R.id.conversation_unread);
        }
    }

    /**
     * 点击后将未读消息置为0
     *
     * @param id Conversation表的主键
     */
    private void updateConversationDB(long id) {
        ConversationDao conversationDao = GreenDAOUtils.getInstance(context).getConversationDao();
        Conversation result = conversationDao.queryBuilder()
                .where(ConversationDao.Properties.Id.eq(id))
                .build()
                .unique();
        if (result != null) {
            result.setUnReadNum(0);
            conversationDao.update(result);
            EventBus.getDefault().post(new Conversation());
        }
    }
}
