package de.htwberlin.f4.ai.ma.indoorroutefinder.paperchase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.indoorroutefinder.R;
import de.htwberlin.f4.ai.ma.indoorroutefinder.node.Node;
import de.htwberlin.f4.ai.ma.indoorroutefinder.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.indoorroutefinder.persistence.DatabaseHandlerFactory;

public class AddCluesActivity extends AppCompatActivity {

    private ListView listView;
    ImageView checkImage;
    ArrayList<Node> nodeList;
    ArrayList<Node> checkedNodesList;
    DatabaseHandler databaseHandler;
    ArrayAdapter<Node> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clues);
        setTitle("Orte hinzufügen");
        listView = (ListView) findViewById(R.id.clue_list_add);
        checkImage = (ImageView) findViewById(R.id.add_clues_item_checkimage);
        databaseHandler = DatabaseHandlerFactory.getInstance(this);
        nodeList = new ArrayList<>();
        checkedNodesList = new ArrayList<>();
        nodeList.addAll(databaseHandler.getAllNodes());
        arrayAdapter = new ClueAdapter(this, R.layout.add_from_all_clues_list_item, nodeList); //TODO custom item adapter
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkImage = (ImageView) view.findViewById(R.id.add_clues_item_checkimage);
                if(!checkedNodesList.contains(nodeList.get(position))) {
                    if(checkImage != null) {
                        checkImage.setImageResource(R.drawable.ic_check_box_black_24dp);
                    }
                    checkedNodesList.add(nodeList.get(position));

                }
                else{
                    checkedNodesList.remove(nodeList.get(position));
                    checkImage.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
                }
            }
        });
    }

    private class ClueAdapter extends ArrayAdapter<Node>{
        private int layoutResource;

        public ClueAdapter(@NonNull Context context, int resource, @NonNull List<Node> objects) {
            super(context, resource, objects);
            layoutResource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if(view == null){
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                view = layoutInflater.inflate(layoutResource, null);
            }
            Node clue = getItem(position);

            if(clue != null){
                TextView clueName = (TextView) view.findViewById(R.id.add_clues_item_name);
                TextView clueDescr = (TextView) view.findViewById(R.id.add_clues_item_description);
                if(clueName != null){
                    clueName.setText(clue.getId());
                }
                if(clueDescr != null){
                    clueDescr.setText(clue.getDescription());
                }
            }
            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_clues, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart:
                Intent intent = new Intent();
                if(checkedNodesList.isEmpty()){
                    setResult(RESULT_CANCELED, intent);
                    Toast.makeText(getApplicationContext(),"Keine Clues hinzugefügt...", Toast.LENGTH_SHORT).show();
                }
                else {
                    Bundle clueBundle = new Bundle();
                    clueBundle.putSerializable("clues", checkedNodesList);
                    intent.putExtra("clues", clueBundle);
                    setResult(RESULT_OK, intent);
                }
                finish();
                return true;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return false;
            default:
                return true;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}