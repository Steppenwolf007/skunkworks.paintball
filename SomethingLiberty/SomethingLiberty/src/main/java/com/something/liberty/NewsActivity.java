package com.something.liberty;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Pringle
 */
public class NewsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        getSupportActionBar().setTitle(getString(R.string.title_activity_news));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String newsItemsJson = sharedPreferences.getString(getString(R.string.pref_news_store),null);
        List<String> allNewsItems = new ArrayList<String>();
        if(newsItemsJson != null)
        {
            try
            {
                JSONArray jsonNewsArray = new JSONArray(newsItemsJson);
                for(int i = 0; i < jsonNewsArray.length(); i++)
                {
                    allNewsItems.add(jsonNewsArray.getString(i));
                }
            }
            catch (JSONException e)
            {
                Log.e("SomethingLiberty", "Failed to parse saved news json");
            }
        }
        ListView newslist = (ListView) findViewById(R.id.newsList);
        NewsListAdapter adapter = new NewsListAdapter(this,allNewsItems);
        newslist.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

/**
 * @author Alexander Pringle
 */
class NewsListAdapter extends ArrayAdapter<String>
{
    private List<String> mNewsItems = null;
    public NewsListAdapter(Context context,List<String> newsItems)
    {
        super(context,0,newsItems);
        this.mNewsItems = newsItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = View.inflate(parent.getContext(),R.layout.news_list_item,null);
        }
        TextView newsTextView = (TextView) convertView.findViewById(R.id.newsItemText);
        String newsText = mNewsItems.get(position);
        newsTextView.setText(newsText);
        return newsTextView;
    }
}
