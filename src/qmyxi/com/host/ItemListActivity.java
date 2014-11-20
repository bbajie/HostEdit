package qmyxi.com.host;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ItemListActivity extends Activity implements OnClickListener {

	private ListView listView;
	private HostAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);

		listView = (ListView)findViewById(R.id.listView1);
		
		adapter = new HostAdapter(this);
		listView.setOnItemClickListener(adapter);
		listView.setAdapter(adapter);
		
		((Button)findViewById(R.id.btn_add)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_del)).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
			case R.id.btn_add:
				adapter.addHost();
				break;
			case R.id.btn_del:
				adapter.delHost();
				break;
		}
	}
	
	
}
