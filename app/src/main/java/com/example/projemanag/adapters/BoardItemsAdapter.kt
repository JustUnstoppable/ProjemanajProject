package com.example.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.databinding.ItemBoardBinding
import com.example.projemanag.models.Board
import de.hdodenhof.circleimageview.CircleImageView

open class BoardItemsAdapter(private  val context: Context,private var list:ArrayList<Board>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener:OnClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_board,parent,false))
    }
    override fun getItemCount(): Int {
        return list.size
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        //a single board should be our model
        val model=list[position]
        if(holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.itemImage)
            holder.itemName.text=model.name
            holder.itemCreatedBy.text="Created By ${model.createdBy}"
            holder.itemView.setOnClickListener{
                if(onClickListener!= null){
                    onClickListener!!.onClick(position,model)
                }
            }
        }
    }
    interface OnClickListener{
        fun onClick(position:Int,model:Board)
    }

    private class MyViewHolder(view: View):RecyclerView.ViewHolder(view){

            var itemImage: CircleImageView = view.findViewById(R.id.iv_board_image)
            var itemName: TextView = view.findViewById(R.id.tv_name)
            var itemCreatedBy: TextView = view.findViewById(R.id.tv_created_by)

    }
}