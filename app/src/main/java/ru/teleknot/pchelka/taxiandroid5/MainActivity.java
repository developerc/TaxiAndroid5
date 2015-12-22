package ru.teleknot.pchelka.taxiandroid5;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    private TextView tvInfo;
    private Button buttonDownload;
    private ProgressBar progressDownload;
    private ImageView ivInet;

    private String filepath = "MyFileStorage";
    private File directory;
    private File dlDir;
    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        directory = this.getDir(filepath, Context.MODE_WORLD_READABLE);

        ivInet = (ImageView) findViewById(R.id.ivFromSite);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        progressDownload = (ProgressBar) findViewById(R.id.progressDownload);
        buttonDownload = (Button) findViewById(R.id.buttonDownload);

        buttonDownload.setOnClickListener(clickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            String urlImage = "http://developer.alexanderklimov.ru/android/images/webview3.png";
            // new GetImageTask().execute(urlImage);
            //находим директорию для хранения загружаемых файлов
            //dlDir = Environment.getExternalStorageDirectory();
            dlDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            Toast toast = Toast.makeText(getBaseContext(),
                    dlDir.toString(), Toast.LENGTH_LONG);
            toast.show();
            new GetImageTask().execute(urlImage);
            // Определяем файл
           // File myFile = new File("cats505.txt");
       /*     File myFile = new File(directory, "cats505.txt");
            // Зададим новый текст для файла
            String new_content = "Cats, kitten";
// Записываем в файл
            try {
                setContent(myFile, new_content);
            } catch (IOException e) {
                e.printStackTrace();
            }  */

            File[] filesArray = directory.listFiles(); // получаем список файлов
            for (File aFilesArray : filesArray) {
                //file = aFilesArray;
                Log.d(TAG, aFilesArray.getName());

            }
        }
    };

    private class GetImageTask extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {
            progressDownload.setProgress(0);
        }

        protected String doInBackground(String... urls) {

            String filename = "android_cat.png";
            File myFile = new File(directory, filename);
            File myFile2 = new File(directory, "new.png");
           //  File myFile = new File(dlDir, filename);

            try {
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int fileSize = connection.getContentLength();

                InputStream is = new BufferedInputStream(url.openStream());
                OutputStream os = new FileOutputStream(myFile);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = is.read(data)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / fileSize));
                    os.write(data, 0, count);
                }

                os.flush();
                os.close();
                is.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                InputStream is2 = new BufferedInputStream(new FileInputStream(myFile));
                OutputStream os2 = new FileOutputStream(myFile2);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = is2.read(data)) != -1) {
                    total += count;
                   // publishProgress((int) (total * 100 / fileSize));
                    os2.write(data, 0, count);
                }

                os2.flush();
                os2.close();
                is2.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return filename;
        }

        protected void onProgressUpdate(Integer... progress) {
            tvInfo.setText(String.valueOf(progress[0]) + "%");
            progressDownload.setProgress(progress[0]);
        }

        protected void onCancelled() {
            Toast toast = Toast.makeText(getBaseContext(),
                    "Error connecting to Server", Toast.LENGTH_LONG);
            toast.show();
        }

        protected void onPostExecute(String filename) {
            progressDownload.setProgress(100);
            tvInfo.setText("Загрузка завершена...");
            File myFile = new File(directory, filename);
            //File myFile = new File(dlDir, filename);
            ivInet.setImageBitmap(BitmapFactory.decodeFile(myFile
                    .getAbsolutePath()));
          directory = new File(directory.toString() + "/android_cat.png");
            dlDir = new File(dlDir.toString() + "/android_cat.png");
            copyFile(directory, dlDir);
        }
    }
    public  boolean copyFile(File source, File dest) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(source));
            bos = new BufferedOutputStream(new FileOutputStream(dest, false));

            byte[] buf = new byte[1024];
            bis.read(buf);

            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                return false;
            }
        }
        return  true;
    }

     public void setContent(File file, String content)
            throws FileNotFoundException, IOException {
        if (file == null) {
            throw new IllegalArgumentException("File should not be null.");
        }
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist: " + file);
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Should not be a directory: " + file);
        }
        if (!file.canWrite()) {
            throw new IllegalArgumentException("File cannot be written: " + file);
        }

        // используем буферизацию
        Writer output = new BufferedWriter(new FileWriter(file));
        try {
            //FileWriter always assumes default encoding is OK!
            output.write(content);
        }
        finally {
            output.close();
        }
    }
}
