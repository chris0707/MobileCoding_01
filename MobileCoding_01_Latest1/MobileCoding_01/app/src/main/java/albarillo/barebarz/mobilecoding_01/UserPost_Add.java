package albarillo.barebarz.mobilecoding_01;

import android.app.Activity;
import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.util.HashMap;
import java.util.Map;

public class UserPost_Add extends AppCompatActivity {
    private int userId;
    private String username, title, body, postId;
    private EditText editTitle, editBody;
    private boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post_add);

        final Button btnPost = findViewById(R.id.btnPost);
        editTitle = findViewById(R.id.editTitle);
        editBody = findViewById(R.id.editBody);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getInt("userId");
            username = extras.getString("username");
            btnPost.setText("Post");
            try
            {
                if (extras.getString("title") != null && extras.getString("body") != null) {
                    title = extras.getString("title");
                    body = extras.getString("body");
                    postId = extras.getString("postId");
                    btnPost.setText("Update");
                    isUpdate = true;

                    editTitle.setText(title);
                    editBody.setText(body);
                }
            }
            catch (Exception e)
            {
                //Do nothing
            }
        }

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editTitle.getText().toString().isEmpty() && !editBody.getText().toString().isEmpty())
                {
                    //Post to client
                    if (isUpdate)
                        PutRequest("https://jsonplaceholder.typicode.com/posts/" + postId);
                    else
                        PostRequest("https://jsonplaceholder.typicode.com/posts");
                    //Toast.makeText(getApplicationContext(), "Posted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(), "Please fill in the fields", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void PostRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        //final TextView textView = (TextView) findViewById(R.id.textView);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if (!response.isEmpty())
                            Toast.makeText(getApplicationContext(), "Posted", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "Posted - empty response", Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", Integer.toString(userId));
                params.put("title", editTitle.getText().toString().trim());
                params.put("body", editBody.getText().toString());

                return params;
            }

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
//                return params;
//            }

        };
        queue.add(stringRequest);
    }

    public void PutRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        //final TextView textView = (TextView) findViewById(R.id.textView);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if (!response.isEmpty())
                            Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "Updated - empty response", Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", editTitle.getText().toString().trim());
                params.put("body", editBody.getText().toString());

                return params;
            }

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
//                return params;
//            }

        };
        queue.add(stringRequest);
    }
}
