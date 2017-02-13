package ua.com.kindershop.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import ua.com.kindershop.asynktasks.DownloadTask;
import ua.com.kindershop.asynktasks.FetchCategoryTask;
import ua.com.kindershop.asynktasks.FetchProductTask;
import ua.com.kindershop.model.Category;
import ua.com.kindershop.model.Product;
import ua.com.kindershop.ui.R;

public class ProductsAdapter extends BaseAdapter {
    private Context mContext;
    public LayoutInflater inflater = null;
    private Product[] CachedData;
    private Activity activity;
    List<? extends Map<String, ?>> data;// array for caching users

    public ProductsAdapter(Context context,
                           List<? extends Map<String, ?>> data, int resource, String[] from,
                           int[] to) {
        super();
        //super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.CachedData = new Product[this.data.size()];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        Product cachedProduct = CachedData[position];
        ViewHolder viewHolder;
        if (vi == null) {
            vi = inflater.inflate(R.layout.list_item_product, null);
            viewHolder = new ViewHolder();
            viewHolder.Name = (TextView) vi.findViewById(R.id.name);
            viewHolder.Id = (TextView) vi.findViewById(R.id.pid);
            viewHolder.Subcat = (TextView) vi.findViewById(R.id.subcat);
            //viewHolder.spinner = (ProgressBar) vi.findViewById(android.R.id.progress);
            viewHolder.imageCategory = (ImageView) vi.findViewById(R.id.cat_image);
            viewHolder.Price = (TextView) vi.findViewById(R.id.list_price);
            vi.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) vi.getTag();
        }
        if (cachedProduct == null) {
            viewHolder.Name.setText("");
            viewHolder.Id.setText("");
            viewHolder.Subcat.setText("");
            viewHolder.Price.setText("");
            //viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(
            //        null,
            //        null,
            //        activity.getResources().getDrawable(
            //               R.drawable.facebook_icon), null);
            //viewHolder.spinner.setVisibility(View.VISIBLE);
            HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
            viewHolder.imageCategory.setImageDrawable(mContext.getResources().getDrawable(R.drawable.spin_custom));
            new FetchProductTask(mContext, this, position,
                    data,
                    viewHolder.imageCategory,
                    viewHolder.Name,
                    viewHolder.Id,
                    viewHolder.Subcat,
                    viewHolder.Price)
                    .execute(new Void[]{});
        } else {
            viewHolder.Name.setText(cachedProduct.getName());
            viewHolder.Id.setText(cachedProduct.getId());
            viewHolder.Subcat.setText(cachedProduct.getSubcat());
            viewHolder.imageCategory.setImageBitmap(cachedProduct.getBitmap());
            viewHolder.Price.setText(cachedProduct.getPrice());
            //viewHolder.spinner.setVisibility(View.GONE);
            //viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(
            //        new BitmapDrawable(activity.getResources(), cachedUser
            //                .getBitmap()), null, activity.getResources()
            //                .getDrawable(R.drawable.facebook_icon), null);
        }
        /*
        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);

        new DownloadTask((ImageView) vi.findViewById(R.id.cat_image))
                .execute((String) data.get("uri"));
        TextView text = (TextView)vi.findViewById(R.id.name);
        String name = (String) data.get("name");
        text.setText(name);
        text = (TextView)vi.findViewById(R.id.pid);
        name = (String) data.get("id");
        text.setText(name);
        text = (TextView)vi.findViewById(R.id.subcat);
        name = (String) data.get("subcat");
        text.setText(name);
        text = (TextView)vi.findViewById(R.id.list_price);
        name = (String) data.get("price") + " грн.";
        text.setText(name);
        */
        return vi;
    }


    private static class ViewHolder {
        public TextView Name;
        public TextView Id;
        public TextView Subcat;
        public ProgressBar spinner;
        public ImageView imageCategory;
        public TextView Price;
    }


    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Product[] getProducts() {
        return CachedData;
    }



}