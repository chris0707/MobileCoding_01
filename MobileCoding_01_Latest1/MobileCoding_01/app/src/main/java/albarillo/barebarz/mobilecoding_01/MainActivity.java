package albarillo.barebarz.mobilecoding_01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView lvUsers;
    HashMap<String, String> hash_map = new HashMap<String, String>();
    List<HashMap<String, String>> listItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvUsers = findViewById(R.id.lvUsers);


        //Toast.makeText(this, "Users retrieved", Toast.LENGTH_SHORT).show();
        //Call users here
        GetRequest("https://jsonplaceholder.typicode.com/users");

        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(getApplicationContext(), listItems.get(position).get("First") + "|" +
                        listItems.get(position).get("id"), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, UserPost.class); //explicit intent
                intent.putExtra("userId", Integer.parseInt(listItems.get(position).get("id")));
                intent.putExtra("username", listItems.get(position).get("First"));
                startActivity(intent);
            }
        });
    }


    public void PopulateUsers()
    {
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[]{"First", "Second"},
                new int[]{R.id.text1, R.id.text2});
        for (Map.Entry<String, String> kvp : hash_map.entrySet())
        {
            //assign a hashmap to the list
            HashMap<String, String> res = new HashMap<>();
            String[] temp1 = kvp.getKey().split("\\|");
            res.put("First", kvp.getKey().split("\\|")[0]);
            res.put("Second", kvp.getValue());
            res.put("id", kvp.getKey().split("\\|")[1]);
            listItems.add(res);
        }

        lvUsers.setAdapter(adapter);
    }

    public void GetRequest(String url)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        final TextView textView = (TextView) findViewById(R.id.textView);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                             JSONArray jsonA = new JSONArray(response);

                             for(int i = 0; i < jsonA.length(); i++)
                             {
                                 JSONObject jsnObj = jsonA.getJSONObject(i);
                                 String fa = jsnObj.getJSONObject("address").get("street").toString() + ", " +
                                         jsnObj.getJSONObject("address").get("suite").toString() + ", " +
                                         jsnObj.getJSONObject("address").get("city").toString() + ", " +
                                         jsnObj.getJSONObject("address").get("zipcode").toString();
                                 hash_map.put(jsnObj.get("username").toString() + "|" + jsnObj.get("id").toString(),
                                         fa);
                             }

                            if (hash_map.size() > 0)
                                PopulateUsers();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getBaseContext(), e.getMessage().substring(0,500), Toast.LENGTH_SHORT).show();
                        }

                        //textView.setText("Response is: "+ response.substring(0,500));

                        Toast.makeText(getBaseContext(), "Users retrieved", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("That didn't work!");
                //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

}

/**
 *
 * Android app TODO:
 *
 * 1. Create UI to display a list of users from web service. Done.
 * https://jsonplaceholder.typicode.com/users
 * fields needed:
 * - username, FULL address
 * functionality:
 * - call web service for each load
 *
 * 2. Add functionality so that the user can select from the list and view all posts by that user. Done
 * https://jsonplaceholder.typicode.com/posts?userId={userId} //ID = 2
 * fields needed:
 * - title, body
 * functionality:
 * - all the user to select from the list (#1) and display its posts (title, body)
 *
 * 3. Add functionality so that the user can create a new post by the user they have selected. Done
 * https://jsonplaceholder.typicode.com/posts
 * fields needed:
 * - request form body maybe - userid, title, body?
 * functionality:
 * - allows the user to create new post inside the posts list window and then post to web service.
 * - mock post?
 *
 *
 * 4. Add functionality so that the user can edit or delete a post (PUT or PATCH, DELETE). Done
 * https://jsonplaceholder.typicode.com/posts/postId
 *
 * 5. Add a functionality to allow the user to sort the list of posts in ascending and descending alphabetical order by title. Done
 * functionality:
 * - inside the popup box? -> Add a functionality to sort alphabetically by title.
 */
