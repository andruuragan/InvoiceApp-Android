package com.example.invoce.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.invoce.model.Product;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class CatalogDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "catalog.db";
    private static final int DATABASE_VERSION = 1;

    private final Context ctx;


    public CatalogDbHelper(Context ctx) throws IOException {

        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);

        this.ctx = ctx;

        copyDatabaseIfNeeded();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // база уже есть в assets
    }


    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {
        // обновление базы пока не используется
    }


    private void copyDatabaseIfNeeded() throws IOException {

        java.io.File dbFile =
                ctx.getDatabasePath(DATABASE_NAME);


        if (!dbFile.exists()) {

            java.io.File parent =
                    dbFile.getParentFile();

            if (parent != null) {
                parent.mkdirs();
            }


            InputStream input =
                    ctx.getAssets().open(DATABASE_NAME);


            FileOutputStream output =
                    new FileOutputStream(dbFile);


            byte[] buffer = new byte[1024];

            int length;


            while ((length = input.read(buffer)) > 0) {

                output.write(buffer, 0, length);
            }


            output.flush();

            output.close();

            input.close();
        }
    }


    public List<Product> getAllProducts()
            throws IOException {


        List<Product> products =
                new ArrayList<>();


        SQLiteDatabase db =
                getReadableDatabase();


        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM catalog",
                        null
                );


        try {

            if (cursor.moveToFirst()) {


                do {


                    Product product =
                            new Product(

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow("name")
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow("type")
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow("thickness")
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow("grade")
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow("diameter")
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow("casing")
                                    ),

                                    cursor.getString(
                                            cursor.getColumnIndexOrThrow("chimneyType")
                                    ),

                                    cursor.getDouble(
                                            cursor.getColumnIndexOrThrow("price")
                                    )
                            );


                    products.add(product);


                } while (cursor.moveToNext());
            }


        } finally {

            cursor.close();
        }


        return products;
    }
}