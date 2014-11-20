package qmyxi.com.host;

import java.util.ArrayList;
import java.util.List;

import qmyxi.com.host.entity.HostEntity;
import qmyxi.com.host.util.Util;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HostAdapter extends BaseAdapter implements OnItemClickListener {

	private List<HostEntity> hostList;
	private Context mContext;
	private LayoutInflater mInflater;

	private List<HostEntity> checkEntity;
	
	public HostAdapter(Context context) {
		hostList = new ArrayList<HostEntity>();
		checkEntity = new ArrayList<HostEntity>();
		
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.refreshData();
	}

	@Override
	public int getCount() {
		return hostList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return hostList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.l_host_list, null);
			holder = new ViewHolder();
			/** 得到各个控件的对象 */
			holder.checkBox = (CheckBox) convertView
					.findViewById(R.id.list_item_checkbox);
			holder.ip = (TextView) convertView.findViewById(R.id.list_item_ip);
			holder.url = (TextView) convertView
					.findViewById(R.id.list_item_url);
			convertView.setTag(holder);// 绑定ViewHolder对象
			
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}
		
		final HostEntity entity = hostList.get(pos);
		
		holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1 &&!checkEntity.contains(entity)){
					checkEntity.add(entity);
				}else if(checkEntity.contains(entity)){
					checkEntity.remove(entity);
				}
			}
		});
		
		
		if (entity != null) {
			holder.ip.setText(entity.getIp());
			holder.url.setText(entity.getUrl());
			
			holder.checkBox.setChecked(checkEntity.contains(entity));
		}else{
			holder.checkBox.setChecked(false);
		}

		return convertView;
	}

	/** 存放控件 */
	public final class ViewHolder {
		public TextView url;
		public TextView ip;
		public CheckBox checkBox;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		final Dialog dialog = createDialog("edit",this.mContext);
		
		final TextView textIp = (TextView) dialog.findViewById(R.id.dialog_ip);
		final TextView textUrl = (TextView) dialog.findViewById(R.id.dialog_url);
		final TextView textDesc = (TextView) dialog.findViewById(R.id.dialog_desc);
		
		final HostEntity beforEntity = this.hostList.get(position);
		textIp.setText(beforEntity.getIp()==null?"":beforEntity.getIp());
		textUrl.setText(beforEntity.getUrl()==null?"":beforEntity.getUrl());
		String desc = beforEntity.getDesc();
		if(desc!=null && desc.startsWith("#")){
			desc = desc.substring(1);
		}
		textDesc.setText(beforEntity.getDesc()==null?"":desc);
		
		dialog.findViewById(R.id.dialog_yes).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						HostEntity entity = new HostEntity();
						entity.setIp(textIp.getText().toString().trim());
						entity.setUrl(textUrl.getText().toString().trim());
						entity.setDesc(textDesc.getText().toString().trim());
						Util.editHost(beforEntity,entity);
						refreshData();
						dialog.dismiss();
					}
				});

		dialog.show();
	}

	
	private Dialog createDialog(String title,Context context){
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.l_host_dialog);
		dialog.setTitle(title);
		dialog.findViewById(R.id.dialog_no).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});
		return dialog;
	}
	
	public void refreshData() {
		this.hostList = Util.getHost();
		checkEntity.clear();
		this.notifyDataSetChanged();
	}
	
	/**
	 * 添加host
	 */
	public void addHost(){
		final Dialog dialog = createDialog("add",this.mContext);
		
		final TextView textIp = (TextView) dialog.findViewById(R.id.dialog_ip);
		final TextView textUrl = (TextView) dialog.findViewById(R.id.dialog_url);
		final TextView textDesc = (TextView) dialog.findViewById(R.id.dialog_desc);
		dialog.findViewById(R.id.dialog_yes).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						HostEntity entity = new HostEntity();
						entity.setIp(textIp.getText().toString().trim());
						entity.setUrl(textUrl.getText().toString().trim());
						entity.setDesc(textDesc.getText().toString().trim());
						Util.addHost(entity);
						refreshData();
						dialog.dismiss();
					}
				});

		dialog.show();
	}
	
	public void delHost(){
		Util.delHost(checkEntity);
		this.refreshData();
	}

	
}
