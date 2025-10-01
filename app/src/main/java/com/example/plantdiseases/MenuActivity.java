package com.example.plantdiseases;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuActivity extends AppCompatActivity {

    DisplayMetrics displayMetrics = new DisplayMetrics();
    private int screenWidth, screenHeight;
    private TextView textWelcome, textInfo;
    private FirebaseFirestore db;
    private Toolbar toolbar;

    private String InforamitonDisease = "Информация о болезни";
    private String htmlText;
    private String currentLanguage = "ru"; // язык по умолчанию

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Инициализация Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(InforamitonDisease);
        }

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        textWelcome = findViewById(R.id.textWelcome);
        textInfo = findViewById(R.id.textInfo);

        if (textWelcome == null || textInfo == null) {
            Log.e("MENU_ACTIVITY", "Views not found: textWelcome=" + (textWelcome == null ? "null" : "found") +
                    ", textInfo=" + (textInfo == null ? "null" : "found"));
            Toast.makeText(this, "Ошибка: UI элементы не найдены", Toast.LENGTH_SHORT).show();
            return;
        }

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        db = FirebaseFirestore.getInstance();
        if (db == null) {
            Log.e("MENU_ACTIVITY", "Firestore initialization failed");
            Toast.makeText(this, "Ошибка инициализации Firestore", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = getIntent().getStringExtra("category");
        if (category == null || category.isEmpty()) {
            Log.e("MENU_ACTIVITY", "No category provided in Intent");
            textWelcome.setText("Ошибка");
            textInfo.setText("Категория не выбрана");
            Toast.makeText(this, "Категория не выбрана", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            loadUserLanguage(username);
        } else {
            // Если username нет, просто устанавливаем текст
            setLanguageText();
            loadCategoryContent();
        }

        textWelcome.setText(category);
    }

    private void loadUserLanguage(String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(username).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userLanguage = documentSnapshot.getString("language");
                        // ЛОГИРОВАНИЕ ЯЗЫКА ИЗ БАЗЫ ДАННЫХ
                        Log.i("Language", "Language from database: " + userLanguage);

                        if (userLanguage != null) {
                            currentLanguage = userLanguage;
                        }
                        setLanguageText();
                        loadCategoryContent();
                    } else {
                        setLanguageText();
                        loadCategoryContent();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MENU_ACTIVITY", "Error loading user language: " + e.getMessage());
                    setLanguageText();
                    loadCategoryContent();
                });
    }

    private void setLanguageText() {
        if (currentLanguage.equals("en")) {
            InforamitonDisease = "Disease Information";
        } else if (currentLanguage.equals("hy")) {
            InforamitonDisease = "Հիվանդության մասին տեղեկատվություն";
        } else {
            InforamitonDisease = "Информация о болезни";
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(InforamitonDisease);
        }
    }

    private void loadCategoryContent() {
        String category = getIntent().getStringExtra("category");

        if (category.equals("Fungal")) {
            if (currentLanguage.equals("en")) {
                htmlText = "<h2><font color=\"#2E7D32\"><b>Late Blight</b></font></h2><br/>\n" +
                        "<h3>Pathogen</h3>" +
                        "<img src='fungal_reino_protista'/><br/>" +
                        "<p style='font-size: 20px; font-weight: 900; text-align: center;'><i>Phytophthora infestans</i> (oomycete)</p>" +
                        "<h3>Host Range</h3>"+
                        "<p>Tomato, pepper, eggplant, potato, and other solanaceous crops.</p><br/>"+

                        "<h3>Symptoms</h3>" +
                        "<p><b>Leaves:</b></p>" +
                        "<p>⦁ <i>Early signs: small, irregularly shaped, water-soaked spots from light green to dark green appear on the upper side of leaves.</i><br/>" +
                        "⦁ <i>Disease progression: spots rapidly enlarge, becoming brown-black or purplish-black, with a chlorotic (yellowish) zone forming around them.</i><br/>" +
                        "⦁ <i>Late stage: a white, fuzzy coating—pathogen sporulation (Phytophthora infestans)—appears on the underside of leaves along the edges of affected areas.</i></p>" +
                        "<div style = 'display:flex; justify-content: center;'>" +
                        "<img src='fungal_late_blight' width='500' height='200' /><br/>" +
                        "<img src='fungal_phytophthora' width='500' height='200' /><br/>" +
                        "<img src='fungal_phytophthora_infestans_late' width='500' height='200' /><br/>" +
                        "</div>" +

                        "<p><b>Stems and Petioles:</b></p>" +
                        "<p>⦁ <i>Elongated, dark brown or black necrotic lesions form.</i><br/>" +
                        "⦁ <i>Under high humidity, sporulation may occur on the surface of affected tissues.</i><br/>" +
                        "⦁ <i>Affected tissues become brittle, leading to plant lodging.</i></p>" +

                        "<div style = 'display:flex; justify-content: center;'>" +
                        "<img src='fungal_phytophthora_infestans_root' width='500' height='200' /><br/>" +
                        "<img src='fungal_late_blight_3' width='500' height='200' /><br/>" +
                        "</div>" +

                        "<p><b>Fruits:</b></p>" +
                        "<p><i>⦁ Infection usually begins at the top of the fruit.<br/>" +
                        "⦁ Brown, hard spots, sometimes with a gray-green tint, form on the surface.<br/>" +
                        "⦁ Tissues remain firm but may later develop secondary infections and rot.</i></p>" +

                        "<div style = 'display:flex; justify-content: center;'>" +
                        "<img src='fungal_tomato' width='500' height='200' /><br/>" +
                        "<img src='fungal_late_blight_2' width='500' height='200' /><br/>" +
                        "</div>" +

                        "<h3>Epidemiology and Life Cycle</h3>" +
                        "<p><b>Sources of Infection:</b><br/>"+
                        "⦁ Crop residues of tomato, potato, and other solanaceous plants;<br/>" +
                        "⦁ Infected seed tubers (in potatoes);<br/>" +
                        "⦁ Weeds from the solanaceous family (e.g., black nightshade);<br/>" +
                        "⦁ Adjacent plantings of infected crops.</p><br/>" +

                        "<p><b>Development and Spread:</b><br/><br/>" +
                        "⦁ Primary infection occurs when spores contact wet leaves or stems.<br/>" +
                        "⦁ Under high humidity, sporulation forms on affected tissues. Spores spread via wind, raindrops, insects, and mechanically (through humans, equipment, or animals).<br/>" +
                        "⦁ The infection spreads rapidly: new foci can appear within 2–3 days after initial infection.</p>" +

                        "<h4><b>Climatic Conditions Favoring Development:</b></h4>" +
                        "<p>⦁ Optimal temperature: 10–25 °C (with most active development at 15–20 °C).<br/>" +
                        "⦁ Air humidity above 75–80% or prolonged moisture retention on leaves (dew, fog, rain).<br/>" +
                        "⦁ Sharp fluctuations between day and night temperatures.<br/>" +
                        "⦁ Overhead irrigation and dense plantings that hinder ventilation.</p>" +

                        "<h4>Epidemiological Features:</h4>" +
                        "<p>⦁ In open fields, it develops in foci, but in greenhouses, it can become epidemic.<br/>" +
                        "⦁ Under favorable conditions, the disease can destroy a tomato crop in 7–10 days.</p>" +
                        "<h3>Control and Prevention Measures</h3>" +
                        "<h5>Agronomic Measures:</h5>" +
                        "<p>⦁ Crop rotation: return solanaceous crops to the same field no earlier than 3–4 years.<br/>" +
                        "⦁ Complete removal and destruction of crop residues after harvest.<br/>" +
                        "⦁ Use of healthy planting material (certified plants and tubers).<br/>" +
                        "⦁ Ensuring good ventilation in greenhouses and avoiding dense plantings.<br/>" +
                        "⦁ Preferential root irrigation, avoiding overhead watering.<br/>" +
                        "⦁ Maintaining an optimal microclimate (reducing humidity, improving air circulation).</p>" +

                        "<h3>Biological Methods:</h3>" +
                        "<p>⦁ Use of microbiological preparations based on fungi and bacteria for preventive treatments (Trichoderma spp., Bacillus subtilis), which suppress pathogen development.</p>" +
                        "<h4>Chemical Methods:</h4>" +
                        "<p>⦁ Contact fungicides: copper-based preparations (Bordeaux mixture, copper oxychloride, copper hydroxide).<br/>" +
                        "⦁ Systemic fungicides: active ingredients—metalaxyl, mandipropamid, cymoxanil, cyazofamid, etc.<br/>" +
                        "⦁ Application of preparations strictly according to regulations, considering waiting periods and rotating active ingredients to prevent resistance.</p>" +

                        "<h4>Integrated Plant Protection:</h4>" +
                        "<p>⦁ Combination of agronomic, biological, and chemical methods.<br/>" +
                        "⦁ Regular phytosanitary monitoring of crops.<br/>" +
                        "⦁ Use of predictive models (considering temperature, humidity, and precipitation) to determine treatment timings.<br/>" +
                        "⦁ Minimizing chemical use through preventive and biological methods.</p>";
            } else {
                // Русская версия (по умолчанию)
                htmlText = "<h2><font color=\"#2E7D32\"><b>Фитофтороз (Late blight)</b></font></h2><br/>\n" +
                        "<h3>Возбудитель</h3>" +
                        "<img src='fungal_reino_protista'/><br/>" +
                        "<p style='font-size: 20px; font-weight: 900; text-align: center;'><i>Phytophthora infestans</i> (оомицет)</p>" +
                        "<h3>Круг хозяев</h3>"+
                        "<p>Томат, перец, баклажан, картофель и др. паслёновые.</p><br/>"+

                        "<h3>Симптомы</h3>" +
                        "<p><b>Листья:</b></p>" +
                        "<p>⦁ <i>Ранние признаки: на верхней стороне листьев появляются небольшие, неправильной формы, водянистые пятна от светло-зелёного до тёмно-зелёного цвета.</i><br/>" +
                        "⦁ <i>Развитие болезни: пятна быстро увеличиваются, становятся буро-коричневыми или пурпурно-чёрными, вокруг них формируется хлоротичная (желтоватая) зона.</i><br/>" +
                        "⦁ <i>Поздняя стадия: на нижней стороне листьев, по краям поражённых участков, появляется белый пушистый налёт — спороношение патогена (Phytophthora infestans).</i></p>" +
                        "<div style = 'display:flex; justify-content: center;'>" +
                        "<img src='fungal_late_blight' width='500' height='200' /><br/>" +
                        "<img src='fungal_phytophthora' width='500' height='200' /><br/>" +
                        "<img src='fungal_phytophthora_infestans_late' width='500' height='200' /><br/>" +
                        "</div>" +

                        "<p><b>Стебли и черешки:</b></p>" +
                        "<p>⦁ <i>Формируются продолговатые, тёмно-бурые или чёрные некротические поражения.</i><br/>" +
                        "⦁ <i>При высокой влажности возможно спороношение на поверхности поражённых тканей.</i><br/>" +
                        "⦁ <i>В местах поражения ткани становятся ломкими, что приводит к полеганию растений.</i></p>" +

                        "<div style = 'display:flex; justify-content: center;'>" +
                        "<img src='fungal_phytophthora_infestans_root' width='500' height='200' /><br/>" +
                        "<img src='fungal_late_blight_3' width='500' height='200' /><br/>" +
                        "</div>" +

                        "<p><b>Плоды:</b></p>" +
                        "<p><i>⦁ Поражение обычно начинается с верхней части плода.<br/>" +
                        "⦁ На поверхности формируются бурые твёрдые пятна, иногда с серо-зелёным оттенком.<br/>" +
                        "⦁ Ткани остаются плотными, но со временем покрываются вторичной инфекцией и загнивают.</i></p>" +

                        "<div style = 'display:flex; justify-content: center;'>" +
                        "<img src='fungal_tomato' width='500' height='200' /><br/>" +
                        "<img src='fungal_late_blight_2' width='500' height='200' /><br/>" +
                        "</div>" +

                        "<h3>Эпидемиология и жизненный цикл</h3>" +
                        "<p><b>Источники инфекции:</b><br/>"+
                        "⦁ растительные остатки томата, картофеля и других паслёновых;<br/>" +
                        "⦁ заражённые семенные клубни (у картофеля);<br/>" +
                        "⦁ сорняки из семейства паслёновых (например, паслён чёрный);<br/>" +
                        "⦁ соседние посадки заражённых культур.</p><br/>" +

                        "<p><b>Развитие и распространение:</b><br/><br/>" +
                        "⦁ Первичное заражение происходит при контакте спор с влажными листьями или стеблями.<br/>" +
                        "⦁ При высокой влажности на поражённых тканях формируется спороношение. Споры распространяются ветром, дождевыми каплями, насекомыми и механически (через людей, инвентарь, животных).<br/>" +
                        "⦁ Инфекция распространяется очень быстро: новые очаги могут появляться в течение 2–3 дней после первичного заражения.</p>" +

                        "<h4><b>Климатические условия, способствующие развитию:</b></h4>" +
                        "<p>⦁ Оптимальная температура: 10–25 °C (с наиболее активным развитием при 15–20 °C).<br/>" +
                        "⦁ Влажность воздуха выше 75–80% или длительное удержание влаги на листьях (роса, туманы, дожди).<br/>" +
                        "⦁ Резкие перепады дневной и ночной температуры.<br/>" +
                        "⦁ Дождевое орошение и загущенные посадки, препятствующие вентиляции.</p>" +

                        "<h4>Особенности эпидемиологии:</h4>" +
                        "<p>⦁ На открытых посевах развивается очагами, но в теплицах может принимать характер эпифитотии.<br/>" +
                        "⦁ При благоприятных условиях болезнь способна уничтожить урожай томата за 7–10 дней.</p>" +
                        "<h3>Меры борьбы и профилактика</h3>" +
                        "<h5>Агротехнические меры:</h5>" +
                        "<p>⦁ Соблюдение севооборота: возвращение паслёновых культур на прежнее место не ранее чем через 3–4 года.<br/>" +
                        "⦁ Полное удаление и уничтожение растительных остатков после уборки урожая.<br/>" +
                        "⦁ Использование здорового посадочного материала (сертифицированные растения и клубни).<br/>" +
                        "⦁ Обеспечение хорошей вентиляции в теплицах, предотвращение загущённых посадок.<br/>" +
                        "⦁ Полив преимущественно под корень, исключение дождевого орошения.<br/>" +
                        "⦁ Поддержание оптимального микроклимата (снижение влажности, улучшение циркуляции воздуха).</p>" +

                        "<h3>Биологические методы:</h3>" +
                        "<p>⦁ Использование микробиологических препаратов на основе грибов и бактерий для профилактических обработок (Trichoderma spp., Bacillus subtilis), которые подавляют развитие патогена.</p>" +
                        "<h4>Химические методы:</h4>" +
                        "<p>⦁ Контактные фунгициды: медьсодержащие препараты (бордосская смесь, хлорокись меди, гидроксид меди).<br/>" +
                        "⦁ Системные фунгициды: действующие вещества — металаксил, мандипропамид, цимоксанил, циазофамид и др.<br/>" +
                        "⦁ Применение препаратов строго по регламенту с учётом периода ожидания и ротации действующих веществ для предотвращения резистентности.</p>" +

                        "<h4>Интегрированная защита растений:</h4>" +
                        "<p>⦁ Сочетание агротехнических, биологических и химических приёмов.<br/>" +
                        "⦁ Регулярный фитосанитарный мониторинг посевов.<br/>" +
                        "⦁ Применение прогнозных моделей (учёт температуры, влажности и осадков) для определения сроков обработок.<br/>" +
                        "⦁ Минимизация применения химических средств за счёт профилактических и биологических методов.</p>";
            }

            // Устанавливаем HTML текст
            textInfo.setText(HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    int resId = getResources().getIdentifier(source, "drawable", getPackageName());
                    if (resId == 0) {
                        Log.w("MENU_ACTIVITY", "Image resource not found: " + source);
                        Drawable placeholder = getResources().getDrawable(android.R.drawable.ic_menu_gallery);
                        placeholder.setBounds(0, 0, 300, 200);
                        return placeholder;
                    }

                    Drawable drawable = getResources().getDrawable(resId);

                    if (source.equals("fungal_reino_protista")) {
                        int originalWidth = drawable.getIntrinsicWidth();
                        int originalHeight = drawable.getIntrinsicHeight();

                        int maxWidth = 1000;
                        int newHeight = (originalHeight * maxWidth) / originalWidth;

                        int left = (screenWidth - maxWidth) / 2;
                        int right = (screenWidth + maxWidth) / 2;

                        drawable.setBounds(left, 0, right, newHeight);
                    }
                    else if (source.equals("fungal_phytophthora") || source.equals("fungal_phytophthora_infestans_late") || source.equals("fungal_late_blight")) {
                        int originalWidth = drawable.getIntrinsicWidth();
                        int originalHeight = drawable.getIntrinsicHeight();

                        int maxWidth = 1000;
                        int newHeight = (originalHeight * maxWidth) / originalWidth;

                        int left = (screenWidth - maxWidth) / 2;
                        int right = (screenWidth + maxWidth) / 2;

                        drawable.setBounds(left, 0, right, newHeight);
                    }
                    else if (source.equals("fungal_phytophthora_infestans_root") || source.equals("fungal_late_blight_3")) {
                        int originalWidth = drawable.getIntrinsicWidth();
                        int originalHeight = drawable.getIntrinsicHeight();

                        int maxWidth = 1000;
                        int newHeight = (originalHeight * maxWidth) / originalWidth;

                        int left = (screenWidth - maxWidth) / 2;
                        int right = (screenWidth + maxWidth) / 2;

                        drawable.setBounds(left, 0, right, newHeight);

                    }
                    else if (source.equals("fungal_tomato") || source.equals("fungal_late_blight_2")) {
                        int originalWidth = drawable.getIntrinsicWidth();
                        int originalHeight = drawable.getIntrinsicHeight();

                        int maxWidth = 1000;
                        int newHeight = (originalHeight * maxWidth) / originalWidth;

                        int left = (screenWidth - maxWidth) / 2;
                        int right = (screenWidth + maxWidth) / 2;

                        drawable.setBounds(left, 0, right, newHeight);
                    }
                    else if (source.equals("fungal_phytophthora_infestans")) {
                        drawable.setBounds( 700, 0, 700, 700);
                    } else {
                        drawable.setBounds(0, 0, 300, 200);
                    }

                    return drawable;
                }
            }, null));
        } else {
            textInfo.setText("Информация о категории " + category + " пока недоступна.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        String username = getIntent().getStringExtra("USERNAME");
        Intent intent = new Intent(this, CategoryListActivity.class);
        if (username != null) {
            intent.putExtra("USERNAME", username);
        }
        startActivity(intent);
        finish();
    }
}