package com.nexters.godofmemo;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nexters.godofmemo.dao.MemoDAO;
import com.nexters.godofmemo.object.Memo;

public class BackupActivity extends Activity implements OnClickListener {

	private EditText email_address;
	private ImageView send_email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_backup);

		email_address = (EditText) findViewById(R.id.backup_email);
		send_email = (ImageView) findViewById(R.id.btn_backup_done);

		email_address.setOnClickListener(this);
		send_email.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_backup_done:
			sendMemoBackup(); // 메모생성!!
			break;
		}

	}

	/**
	 * backup.
	 */
	private void sendMemoBackup() {
		// load memo..
		MemoDAO memoDao = new MemoDAO(getApplicationContext());
		ConcurrentLinkedQueue<Memo> memoList = memoDao.getMemoList();

		StringBuffer sb = new StringBuffer();
		for (Memo m : memoList) {
			sb.append("--------------------------------\n");
			sb.append(m.toHumanReadableString());
			sb.append("--------------------------------\n");
			sb.append("\n");
		}

		String backupContent = sb.toString();

		Intent it = new Intent(Intent.ACTION_SEND);
		it.setType("plain/text");

		String emailAddress = email_address.getText().toString();

		String[] toEmails = { emailAddress };
		it.putExtra(Intent.EXTRA_EMAIL, toEmails);

		it.putExtra(Intent.EXTRA_SUBJECT, "Backup");
		it.putExtra(Intent.EXTRA_TEXT, backupContent);
		// 파일첨부
		try {
			startActivity(Intent.createChooser(it, "메일보냄"));
		} catch (android.content.ActivityNotFoundException ex) {
			ex.printStackTrace();
			Toast.makeText(this, "문제가 발생했습니다.	", Toast.LENGTH_SHORT).show();
			// TODO 메시지 확인필요
		}

		// send to email.

	}

}
