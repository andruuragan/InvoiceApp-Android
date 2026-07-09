package com.example.invoce;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invoce.adapter.ProductAdapter;
import com.example.invoce.data.CatalogDbHelper;
import com.example.invoce.model.Product;

import java.util.List;
import android.widget.TextView;
import android.widget.EditText;

import java.util.ArrayList;

import com.example.invoce.InvoiceItem;
import com.example.invoce.adapter.InvoiceAdapter;
import java.text.DecimalFormat;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;

import android.view.KeyEvent;
import android.view.View;
import androidx.drawerlayout.widget.DrawerLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import java.util.stream.Collectors;
import androidx.core.view.GravityCompat;
import android.widget.Button;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    private RecyclerView recyclerCatalog;

    private List<Product> allProducts;
    private Product selectedProduct;
    private RecyclerView recyclerInvoice;

    private InvoiceAdapter invoiceAdapter;
    private ProductAdapter productAdapter;
    private DrawerLayout drawerLayout;

    private Spinner spinnerFilter1;
    private Spinner spinnerFilter2;
    private Spinner spinnerFilter3;
    private Spinner spinnerFilter4;
    private Spinner spinnerFilter5;
    private Spinner spinnerFilter6;
    private List<Product> displayProducts = new ArrayList<>();


    private final List<InvoiceItem> invoiceItems = new ArrayList<>();


    private TextView tvTotalSum;
    private TextView tvTotalWithDiscount;

    private EditText etDiscount;
    private double currentTotal = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);

        ImageButton btnOpenFilter = findViewById(R.id.btnOpenFilter);

        btnOpenFilter.setZ(1000);
        btnOpenFilter.bringToFront();

        btnOpenFilter.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });


        recyclerCatalog = findViewById(R.id.recyclerCatalog);
        recyclerCatalog.setLayoutManager(new LinearLayoutManager(this));

        recyclerInvoice = findViewById(R.id.recyclerInvoice);
        recyclerInvoice.setLayoutManager(new LinearLayoutManager(this));

        tvTotalSum = findViewById(R.id.tvTotalSum);
        tvTotalWithDiscount = findViewById(R.id.tvTotalWithDiscount);
        etDiscount = findViewById(R.id.etDiscount);

        spinnerFilter1 = findViewById(R.id.spinnerFilter1);
        spinnerFilter2 = findViewById(R.id.spinnerFilter2);
        spinnerFilter3 = findViewById(R.id.spinnerFilter3);
        spinnerFilter4 = findViewById(R.id.spinnerFilter4);
        spinnerFilter5 = findViewById(R.id.spinnerFilter5);
        spinnerFilter6 = findViewById(R.id.spinnerFilter6);


        etDiscount.setSingleLine(true);
        etDiscount.setImeOptions(6);

        etDiscount.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == 6 ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                etDiscount.clearFocus();

                InputMethodManager imm =
                        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(
                        etDiscount.getWindowToken(),
                        0
                );

                return true;
            }

            return false;
        });


        invoiceAdapter = new InvoiceAdapter(
                invoiceItems,
                position -> {
                },
                total -> {

                    currentTotal = total;

                    updateTotals(total);

                }
        );
        etDiscount.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {

                if (!s.toString().isEmpty()) {

                    try {

                        double discount = Double.parseDouble(s.toString());

                        if (discount > 100) {

                            etDiscount.setText("100");
                            etDiscount.setSelection(
                                    etDiscount.getText().length()
                            );

                            return;
                        }

                    } catch (Exception ignored) {
                    }

                }


                updateTotals(currentTotal);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        recyclerInvoice.setAdapter(invoiceAdapter);
        ImageButton btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(v -> {

            if (selectedProduct == null) {
                Toast.makeText(this, "Выберите товар", Toast.LENGTH_SHORT).show();
                return;
            }

            showQuantityDialog(selectedProduct);
        });

        ImageButton btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {

            int position = invoiceAdapter.getSelectedPosition();

            if (position == -1) {

                Toast.makeText(
                        this,
                        "Выберите товар в накладной",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Удаление товара")
                    .setMessage("Вы точно хотите удалить этот товар из накладной?")
                    .setNegativeButton("Отмена", null)
                    .setPositiveButton("Удалить", (dialog, which) -> {

                        invoiceAdapter.removeItem(position);

                    })
                    .show();

        });
        ImageButton btnClear = findViewById(R.id.btnClear);

        btnClear.setOnClickListener(v -> {

            if (invoiceAdapter.getItemCount() == 0) {

                Toast.makeText(
                        this,
                        "Накладная уже пустая",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            new AlertDialog.Builder(this)
                    .setTitle("Очистить накладную")
                    .setMessage("Вы точно хотите удалить все товары из накладной?")
                    .setNegativeButton("Отмена", null)
                    .setPositiveButton("Очистить", (dialog, which) -> {

                        invoiceAdapter.clearAll();
                        etDiscount.setText("");


                    })
                    .show();

        });
        ImageButton btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            saveInvoiceToExcel();
        });


        loadCatalog();
        setupFilters();

        Button btnApplyFilters =
                findViewById(R.id.btnApplyFilters);
        Button btnResetFilters =
                findViewById(R.id.btnResetFilters);


        btnApplyFilters.setOnClickListener(v -> {

            applyFilters();

            drawerLayout.closeDrawer(GravityCompat.START);

        });
        btnResetFilters.setOnClickListener(v -> {

            resetFilters();


        });
    }

    private void showQuantityDialog(Product product) {

        EditText input = new EditText(this);
        input.setHint("Количество");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setText("1");

        new AlertDialog.Builder(this)
                .setTitle("Введите количество")
                .setView(input)
                .setPositiveButton("Добавить", (dialog, which) -> {

                    int qty = 1;

                    try {
                        qty = Integer.parseInt(input.getText().toString());
                    } catch (Exception ignored) {
                    }

                    if (qty <= 0) {
                        Toast.makeText(this, "Некорректное количество", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    invoiceAdapter.addItem(new InvoiceItem(product, qty));

                    recyclerInvoice.scrollToPosition(
                            invoiceAdapter.getItemCount() - 1
                    );
                })
                .setNegativeButton("Отмена", null)
                .show();
    }


    private void loadCatalog() {

        try {

            CatalogDbHelper dbHelper =
                    new CatalogDbHelper(this);


            allProducts =
                    dbHelper.getAllProducts();


            displayProducts = new ArrayList<>(allProducts);


            productAdapter = new ProductAdapter(
                    displayProducts,
                    product -> selectedProduct = product
            );

            recyclerCatalog.setAdapter(productAdapter);


        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Ошибка загрузки каталога: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

        }

    }

    private void updateTotals(double total) {

        DecimalFormat df = new DecimalFormat("#.##");

        tvTotalSum.setText(df.format(total));

        double discount = 0;

        try {

            discount = Double.parseDouble(
                    etDiscount.getText().toString()
            );

        } catch (Exception ignored) {
        }

        double result =
                total * (1 - discount / 100.0);

        tvTotalWithDiscount.setText(
                df.format(result)
        );
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull android.view.MotionEvent ev) {

        if (ev.getAction() == android.view.MotionEvent.ACTION_DOWN) {

            View view = getCurrentFocus();

            if (view == etDiscount) {

                etDiscount.clearFocus();

                InputMethodManager imm =
                        (InputMethodManager)
                                getSystemService(INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(
                        view.getWindowToken(),
                        0
                );
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private void setupFilters() {

        setSpinner(
                spinnerFilter1,
                new String[]{
                        "Все",
                        "Термо",
                        "Одностінний"
                }
        );


        setSpinner(
                spinnerFilter2,
                new String[]{
                        "Все",
                        "304",
                        "321",
                        "201",
                        "430"
                }
        );


        setSpinner(
                spinnerFilter3,
                new String[]{
                        "Все",
                        "0,5 мм",
                        "0,8 мм",
                        "1 мм",
                        "2 мм"
                }
        );


        setSpinner(
                spinnerFilter4,
                new String[]{
                        "Все",
                        "н/н",
                        "н/оц"
                }
        );


        setSpinner(
                spinnerFilter5,
                new String[]{
                        "Все",
                        "100", "110", "120", "125", "130", "140", "150", "160", "170", "180",
                        "190", "200", "210", "220", "230", "240", "250", "260", "270", "280",
                        "290", "300", "310", "320", "330", "350", "360", "370", "380", "400",
                        "420", "450", "460", "500", "520", "860",
                        "100/160", "110/180", "120/180", "130/200", "140/200",
                        "150/220", "160/220", "180/250", "200/260", "220/280",
                        "230/300", "250/320", "300/360", "350/420", "400/460",
                        "500/560", "100/200", "120/220", "130/230", "140/240",
                        "150/250", "160/260", "180/280", "200/300"
                }
        );


        setSpinner(
                spinnerFilter6,
                new String[]{
                        "Все",
                        "Труба", "Коліно 45°", "Коліно 90°", "Трійник 90°", "Трійник 45°",
                        "Волпер", "Грибок", "Іскрогасник", "Регулятор тяги(Кагла)", "Лійка",
                        "Окапник", "Закінчення димоходу", "Перехід", "Радіатор", "Ревізія",
                        "Розета", "Сітка", "Скоба", "Криза", "Кронштейн",
                        "Розвантажувальна підставка", "Обжимний хомут", "Хомут під розтяжки",
                        "Стіновий хомут", "Монтажний хомут", "Конус", "Термоґрибок", "Дека",
                        "Заглушка", "Старт-сендвіч", "Труба-подовжувач", "Прохід", "Відображувач"
                }
        );
    }

    private void setSpinner(
            @NonNull Spinner spinner,
            String[] values
    ) {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        values
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }

    private void applyFilters() {


        List<Product> filtered =
                new ArrayList<>();


        for (Product p : allProducts) {


            boolean ok = true;


            if (!spinnerFilter1.getSelectedItem().toString().equals("Все")) {

                ok &= p.getChimneyType()
                        .equals(spinnerFilter1.getSelectedItem().toString());

            }


            if (!spinnerFilter2.getSelectedItem().toString().equals("Все")) {

                ok &= p.getGrade()
                        .equals(spinnerFilter2.getSelectedItem().toString());

            }


            if (!spinnerFilter3.getSelectedItem().toString().equals("Все")) {

                ok &= p.getThickness()
                        .equals(spinnerFilter3.getSelectedItem().toString());

            }


            if (!spinnerFilter4.getSelectedItem().toString().equals("Все")) {

                ok &= p.getCasing()
                        .equals(spinnerFilter4.getSelectedItem().toString());

            }


            if (!spinnerFilter5.getSelectedItem().toString().equals("Все")) {

                ok &= p.getDiameter()
                        .equals(spinnerFilter5.getSelectedItem().toString());

            }


            if (!spinnerFilter6.getSelectedItem().toString().equals("Все")) {

                ok &= p.getType()
                        .equals(spinnerFilter6.getSelectedItem().toString());

            }


            if (ok) {
                filtered.add(p);
            }

        }


        productAdapter.updateList(filtered);

    }

    private void resetFilters() {

        spinnerFilter1.setSelection(0);
        spinnerFilter2.setSelection(0);
        spinnerFilter3.setSelection(0);
        spinnerFilter4.setSelection(0);
        spinnerFilter5.setSelection(0);
        spinnerFilter6.setSelection(0);

        displayProducts.clear();
        displayProducts.addAll(allProducts);

        productAdapter.notifyDataSetChanged();
    }

    private void saveInvoiceToExcel() {

        try {

            String fileName = "Invoice_" +
                    new SimpleDateFormat("yyyyMMdd_HHmm",
                            Locale.getDefault()).format(new Date())
                    + ".xlsx";

            // Файл сохранится в общую папку Documents на телефоне
            File folder = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS
            );

            if (!folder.exists()) {
                folder.mkdirs();
            }

            File file = new File(folder, fileName);

            FileOutputStream fos = new FileOutputStream(file);

            Workbook wb = new Workbook(fos, "InvoiceApp", "1.0");
            Worksheet ws = wb.newWorksheet("Накладна");

            Toast.makeText(this,
                    "Позицій: " + invoiceItems.size(),
                    Toast.LENGTH_LONG).show();
            // =======================
            // Заголовок
            // =======================

            ws.value(0, 1, "НАКЛАДНА");

            ws.value(1, 1,
                    "Дата: " +
                            new SimpleDateFormat(
                                    "dd.MM.yyyy HH:mm",
                                    Locale.getDefault()
                            ).format(new Date()));

            // =======================
            // Шапка таблиці
            // =======================

            int startRow = 3;

            ws.value(startRow, 0, "№");
            ws.value(startRow, 1, "Найменування");
            ws.value(startRow, 2, "Ціна");
            ws.value(startRow, 3, "Кіл-ть");
            ws.value(startRow, 4, "Сума");

            double total = 0;

            int row = startRow + 1;

            for (int i = 0; i < invoiceItems.size(); i++) {

                InvoiceItem item = invoiceItems.get(i);

                Product product = item.getProduct();

                double sum = product.getPrice() * item.getQuantity();

                total += sum;

                ws.value(row, 0, i + 1);
                ws.value(row, 1, product.displayNameLength());

                ws.value(row, 2,
                        String.format(Locale.US,
                                "%.2f грн",
                                product.getPrice()));

                ws.value(row, 3, item.getQuantity());

                ws.value(row, 4,
                        String.format(Locale.US,
                                "%.2f грн",
                                sum));

                row++;
            }

            // =======================
            // Разом
            // =======================

            ws.value(row, 1, "Разом");
            ws.value(row, 4,
                    String.format(Locale.US,
                            "%.2f грн",
                            total));

            row++;

            // =======================
// =======================
// Знижка
// =======================

            double discount = 0;

            try {
                String text = etDiscount.getText().toString().trim();
                if (!text.isEmpty()) {
                    discount = Double.parseDouble(text);
                }
            } catch (Exception ignored) {
            }

            double discountValue = total * discount / 100.0;
            double result = total - discountValue;
            String message =
                    "Всього = " + String.format(Locale.US, "%.2f грн", total) +
                            "\nРазом зі знижкою = " + String.format(Locale.US, "%.2f грн", result);

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            if (discount > 0) {



                row++;

                ws.value(row, 0, "");
                ws.value(row, 1, "Сума знижки");
                ws.value(row, 2, "");
                ws.value(row, 3, "");
                ws.value(row, 4,
                        String.format(Locale.US, "%.2f грн", discountValue));

                row++;

                ws.value(row, 0, "");
                ws.value(row, 1, "Разом зі знижкою");
                ws.value(row, 2, "");
                ws.value(row, 3,
                        String.format(Locale.US, "%.0f%%", discount));
                ws.value(row, 4,
                        String.format(Locale.US, "%.2f грн", result));
            }
            // =======================
            // Ширина колонок
            // =======================

            ws.width(0, 8);
            ws.width(1, 50);
            ws.width(2, 15);
            ws.width(3, 10);
            ws.width(4, 18);

            wb.finish();
            fos.close();
            android.media.MediaScannerConnection.scanFile(this,
                    new String[]{file.getAbsolutePath()}, null, null);

            Toast.makeText(
                    this,
                    "Файл збережено:\n" + file.getAbsolutePath(),
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    e.toString(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}

