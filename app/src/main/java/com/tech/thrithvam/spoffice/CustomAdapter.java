package com.tech.thrithvam.spoffice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class CustomAdapter extends BaseAdapter {
    private Context adapterContext;
    private static LayoutInflater inflater=null;
    private ArrayList<String[]> objects;
    private String calledFrom;
    private SimpleDateFormat formatted;
    private Calendar cal;
    CustomAdapter(Context context, ArrayList<String[]> objects, String calledFrom) {
        // super(context, textViewResourceId, objects);
        initialization(context, objects, calledFrom);
    }
    void initialization(Context context, ArrayList<String[]> objects, String calledFrom){
        adapterContext=context;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.objects=objects;
        this.filteredObjects=objects;
        this.calledFrom=calledFrom;
//        formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
//        cal= Calendar.getInstance();
    }
    @Override
    public int getCount() {
        return filteredObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Holder {
        //Quotations--------------
        TextView quotationNo,date,customerName,amount,status,emailSent;
        //Enquiries---------------
        TextView enquiryNo,contactTitle,contactPerson,mobile;
        ImageView followUpIcon;
        //Follow Up-------------
        TextView time,description;
        ImageView editIcon;
        //Proforma invoices------
        TextView invoiceNo;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        final int fPos=position;
        switch (calledFrom) {
            //--------------------------for quotation list items------------------
            case Common.QUOTATIONLIST:
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_quotation, null);
                    holder.quotationNo = (TextView) convertView.findViewById(R.id.quotation_no);
                    holder.date = (TextView) convertView.findViewById(R.id.date);
                    holder.customerName = (TextView) convertView.findViewById(R.id.customer_name);
                    holder.amount = (TextView) convertView.findViewById(R.id.amount);
                    holder.status = (TextView) convertView.findViewById(R.id.status);
                    holder.emailSent = (TextView) convertView.findViewById(R.id.email_sent);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.quotationNo.setText((filteredObjects.get(position)[1].equals("null")?"-":filteredObjects.get(position)[1]));
                holder.date.setText((filteredObjects.get(position)[2].equals("null")?"-":filteredObjects.get(position)[2]));
                holder.customerName.setText((filteredObjects.get(position)[3].equals("null")?"-":filteredObjects.get(position)[3]));
                holder.amount.setText((filteredObjects.get(position)[4].equals("null")?"-":adapterContext.getResources().getString(R.string.rupees,String.format(Locale.US,"%.2f",Double.parseDouble(filteredObjects.get(position)[4])))));
                holder.status.setText((filteredObjects.get(position)[5].equals("null")?"-":filteredObjects.get(position)[5]));
                if(!filteredObjects.get(position)[6].equals("null")){
                    if(Boolean.parseBoolean(filteredObjects.get(position)[6])) {
                        holder.emailSent.setText(adapterContext.getResources().getString(R.string.email_sent_colon,adapterContext.getResources().getString(R.string.yes)));
                    }
                    else {
                        holder.emailSent.setText(adapterContext.getResources().getString(R.string.email_sent_colon,adapterContext.getResources().getString(R.string.no)));
                    }
                }
                else {
                    holder.emailSent.setText("-");
                }
                break;
            //--------------------------for enquiry list items------------------
            case Common.ENQUIRYLIST:
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_enquiry, null);
                    holder.enquiryNo = (TextView) convertView.findViewById(R.id.enquiry_no);
                    holder.date = (TextView) convertView.findViewById(R.id.date);
                    holder.contactTitle = (TextView) convertView.findViewById(R.id.contact_title);
                    holder.contactPerson = (TextView) convertView.findViewById(R.id.contact_person_name);
                    holder.customerName = (TextView) convertView.findViewById(R.id.company_name);
                    holder.mobile = (TextView) convertView.findViewById(R.id.mobile);
                    holder.followUpIcon=(ImageView)convertView.findViewById(R.id.follow_up_icon);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.enquiryNo.setText((filteredObjects.get(position)[1].equals("null")?"-":filteredObjects.get(position)[1]));
                holder.date.setText((filteredObjects.get(position)[2].equals("null")?"-":filteredObjects.get(position)[2]));
                holder.contactTitle.setText((filteredObjects.get(position)[3].equals("null")?"":filteredObjects.get(position)[3]));
                holder.contactPerson.setText((filteredObjects.get(position)[4].equals("null")?"-":filteredObjects.get(position)[4]));
                holder.customerName.setText((filteredObjects.get(position)[5].equals("null")?"-":filteredObjects.get(position)[5]));
                holder.mobile.setText((filteredObjects.get(position)[6].equals("null")?"-":filteredObjects.get(position)[6]));
                holder.mobile.setTag((filteredObjects.get(position)[6].equals("null")?"":filteredObjects.get(position)[6]));
                holder.followUpIcon.setTag((filteredObjects.get(position)[0].equals("null")?"":filteredObjects.get(position)[0]));
                break;
            //--------------------------for follow up list items------------------
            case Common.FOLLOWUPLIST:
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_follow_up, null);
                    holder.date = (TextView) convertView.findViewById(R.id.date);
                    holder.time = (TextView) convertView.findViewById(R.id.time);
                    holder.description = (TextView) convertView.findViewById(R.id.description);
                    holder.status = (TextView) convertView.findViewById(R.id.status);
                    holder.editIcon=(ImageView)convertView.findViewById(R.id.edit_icon);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.date.setText((filteredObjects.get(position)[1].equals("null")?"-":filteredObjects.get(position)[1]));
                holder.time.setText((filteredObjects.get(position)[2].equals("null")?"-":filteredObjects.get(position)[2]));
                holder.description.setText((filteredObjects.get(position)[3].equals("null")?"":filteredObjects.get(position)[3]));
                holder.status.setText((filteredObjects.get(position)[4].equals("null")?"-":adapterContext.getResources().getString(R.string.status_colon,filteredObjects.get(position)[4])));
                holder.editIcon.setTag((filteredObjects.get(position)[0].equals("null")?"":filteredObjects.get(position)[0]));
                break;
            //--------------------------for proforma invoices list items------------------
            case Common.PROFORMALIST:
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_proforma, null);
                    holder.customerName = (TextView) convertView.findViewById(R.id.customer_name);
                    holder.invoiceNo = (TextView) convertView.findViewById(R.id.invoice_no);
                    holder.date = (TextView) convertView.findViewById(R.id.date);
                    holder.amount = (TextView) convertView.findViewById(R.id.amount);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.customerName.setText((filteredObjects.get(position)[1].equals("null")?"-":filteredObjects.get(position)[1]));
                holder.invoiceNo.setText((filteredObjects.get(position)[2].equals("null")?"-":filteredObjects.get(position)[2]));
                holder.date.setText((filteredObjects.get(position)[3].equals("null")?"":filteredObjects.get(position)[3]));
                holder.amount.setText((filteredObjects.get(position)[4].equals("null")?"-":adapterContext.getResources().getString(R.string.rupees,String.format(Locale.US,"%.2f",Double.parseDouble(filteredObjects.get(position)[4])))));
                break;
            default:
                break;
        }
        return convertView;
    }

    //Filtering--------------------------------------
    private ItemFilter mFilter = new ItemFilter();
    private ArrayList<String[]> filteredObjects;
    private List<Integer> dataItemPosition;
    Filter getFilter(List<Integer> dataItem) {
        dataItemPosition=dataItem;
        return mFilter;
    }
    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            int count = objects.size();
            final ArrayList<String[]> filteredList = new ArrayList<String[]>(count);

            for (int i = 0; i < count; i++) {
                for(int j=0;j<dataItemPosition.size();j++) {
                    if (objects.get(i)[dataItemPosition.get(j)].toLowerCase().contains(filterString)) {
                        filteredList.add(objects.get(i));
                        break;//found at least one item
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredObjects = (ArrayList<String[]>) results.values;
            notifyDataSetChanged();
        }
    }
}
