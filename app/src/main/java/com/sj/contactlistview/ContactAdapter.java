package com.sj.contactlistview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sj.contactlistview.util.PinyinUtils;
import com.sj.contactlistview.view.IndexView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author SJ
 */
public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TOP = 0;
    private static final int TYPE_START = 1;
    private static final int TYPE_ITEM = 2;
    private static final int TYPE_BOTTOM = 3;

    /**
     * 顶部“新的朋友”之类的总数
     */
    private int topCount = 4;
    /**
     * 底部
     */
    private int bottomCount = 1;

    /**
     * 数据列表，实际使用List<xxxbean>
     */
    private List<String> dataList = new ArrayList<>();
    /**
     * 星标朋友数据列表，实际使用List<xxxbean>
     */
    private List<String> starList = new ArrayList<>();
    /**
     * 返回的首字母列表
     */
    private List<String> firstWordList = new ArrayList<>();
    /**
     * 点击监听
     */
    private OnClickListener listener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_TOP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_top, parent, false);
                return new TopViewHolder(view);
            case TYPE_BOTTOM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_bottom, parent, false);
                return new BottomViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
                return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TopViewHolder) {
            bindTopViewHolder((TopViewHolder) holder, position);
        } else if (holder instanceof ItemViewHolder) {
            bindItemViewHolder((ItemViewHolder) holder, position - topCount);
        } else if (holder instanceof BottomViewHolder) {
            ((BottomViewHolder) holder).textView.setText(String.format(Locale.getDefault(), "%d位联系人", dataList.size()));
        }
    }

    private void bindTopViewHolder(TopViewHolder holder, final int position) {
        holder.textView.setText("新的朋友balabala");
        //处理子项底部分割线
        if (position == topCount - 1) {
            holder.bottomLine.setVisibility(View.GONE);
        } else if (holder.bottomLine.getVisibility() != View.VISIBLE) {
            holder.bottomLine.setVisibility(View.VISIBLE);
        }

        //事件监听
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v, position);
            }
        });
    }

    private void bindItemViewHolder(final ItemViewHolder holder, final int position) {
        //有星标朋友的列表时
        if (starList.size() > position) {
            boolean isItemTop = position == 0;

            holder.textView.setText(starList.get(position));
            //控制子项的索引项是否显示
            setIndexViewIsShow(holder, isItemTop);
            //设置子项的索引项的字符
            if (isItemTop) {
                holder.headTextView.setText("星标朋友");
            }
            //处理子项底部分割线
            holder.bottomLine.setVisibility((position == starList.size() - 1) ? View.GONE : View.VISIBLE);

        } else {
            int hasStatListPosition = position - starList.size();
            boolean isItemTop = firstWordList.indexOf(firstWordList.get(hasStatListPosition)) == hasStatListPosition;

            holder.textView.setText(dataList.get(hasStatListPosition));
            //控制子项的索引项是否显示
            setIndexViewIsShow(holder, isItemTop);
            //设置子项的索引项的字符
            if (isItemTop) {
                holder.headTextView.setText(firstWordList.get(hasStatListPosition));
            }
            //处理子项底部分割线
            holder.bottomLine.setVisibility((firstWordList.lastIndexOf(firstWordList.get(hasStatListPosition)) == hasStatListPosition) ? View.GONE : View.VISIBLE);
        }

        //事件监听
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v, position + topCount);
            }
        });
        holder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClick(v, position + topCount);
                return true;
            }
        });
    }

    /**
     * 控制子项的索引项是否显示
     */
    private void setIndexViewIsShow(ItemViewHolder holder, boolean show) {
        int viewState = (show) ? View.VISIBLE : View.GONE;

        holder.headTextView.setVisibility(viewState);
        holder.headTopLine.setVisibility(viewState);
        holder.headBottomLine.setVisibility(viewState);
    }

    @Override
    public int getItemCount() {
        return topCount + starList.size() + dataList.size() + bottomCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < topCount) {
            return TYPE_TOP;
        } else if (position < topCount + starList.size()) {
            return TYPE_START;
        } else if (position == topCount + starList.size() + dataList.size() + bottomCount - 1) {
            return TYPE_BOTTOM;
        } else {
            return TYPE_ITEM;
        }
    }

    /**
     * 实际使用中数据应该是json，包括联系人姓名，头像图片地址等信息，应改成List<xxxbean>
     * 星标朋友应该是一个标记位，通过这个组成starList
     */
    void setData(List<String> data) {
        this.dataList = data;
        for (String name : data) {
            String firstWord = PinyinUtils.getSurnameFirstLetter(name);
            if (firstWord != null) {
                firstWordList.add(firstWord.toUpperCase());
            }
        }

        //此为演示，伪造‘星标朋友’数据
        starList.add("cc");
        starList.add("dd");
    }

    /**
     * @param word 索引列表划到的单词
     * @return 划到的单词第一次出现的位置
     */
    int getFirstWordListPosition(String word) {
        //索引列表划到↑
        if (word.equals(IndexView.words[0])) {
            return 0;
        }
        //索引列表划到☆
        else if (word.equals(IndexView.words[1]) && starList.size() > 0) {
            return topCount;
        } else if (firstWordList.indexOf(word) >= 0) {
            return firstWordList.indexOf(word) + topCount + starList.size();
        }
        return -1;
    }

    class TopViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;
        private View bottomLine;
        private ConstraintLayout constraintLayout;

        TopViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_top_tv);
            imageView = itemView.findViewById(R.id.item_top_iv);
            bottomLine = itemView.findViewById(R.id.item_top_bottom);
            constraintLayout = itemView.findViewById(R.id.item_top_cl);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textView, headTextView;
        private ImageView imageView;
        private View bottomLine, headBottomLine, headTopLine;
        private ConstraintLayout constraintLayout;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_tv);
            headTextView = itemView.findViewById(R.id.item_head_tv);
            imageView = itemView.findViewById(R.id.item_iv);
            bottomLine = itemView.findViewById(R.id.item_bottom);
            headBottomLine = itemView.findViewById(R.id.item_head_bottom);
            headTopLine = itemView.findViewById(R.id.item_head_top);
            constraintLayout = itemView.findViewById(R.id.item_cl);
        }
    }

    class BottomViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        BottomViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_bottom_tv);
        }
    }

    void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {

        /**
         * 单击监听
         * @param view     v
         * @param position position
         */
        void onClick(View view, int position);

        /**
         * 长按监听
         * @param view     v
         * @param position position
         */
        void onLongClick(View view, int position);
    }
}
