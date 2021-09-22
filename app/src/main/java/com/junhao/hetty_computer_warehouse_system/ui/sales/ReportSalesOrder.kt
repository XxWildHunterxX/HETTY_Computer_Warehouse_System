package com.junhao.hetty_computer_warehouse_system.ui.sales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.barteksc.pdfviewer.PDFView
import com.junhao.hetty_computer_warehouse_system.R


class ReportSalesOrder : Fragment() {

    private lateinit var mPDFView:PDFView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_report_sales_order, container, false)


        mPDFView = view.findViewById<PDFView>(R.id.pdfView)
mPDFView.fromAsset("HettyWarehouseSystem.pdf").load()

        return view
    }


}