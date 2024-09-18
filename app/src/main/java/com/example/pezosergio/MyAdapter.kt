package com.example.pezosergio

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class MyAdapter(private val context: Context): BaseAdapter() {
    private lateinit var lblId: TextView
    private lateinit var lblName: TextView
    private lateinit var lblPosition: TextView
    override fun getCount(): Int {
        return Global.bezierNodes.size
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItem(position: Int): Any {
        return position
    }
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate( R.layout.nodes_rows, parent,false)
        lblId = convertView.findViewById(R.id.lblId)
        lblName = convertView.findViewById(R.id.lblName)
        lblPosition = convertView.findViewById(R.id.lblPosition)

        lblId.text = "Id: " + Global.bezierNodes[position].idNode.toString()
        lblName.text = "Name: " + Global.bezierNodes[position].name
        lblPosition.text = "Position: ( " + Global.bezierNodes[position].positionX.toString() + ", " + Global.bezierNodes[position].positionY.toString() + ")"
        return convertView
    }
}