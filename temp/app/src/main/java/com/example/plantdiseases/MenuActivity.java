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
    private String currentLanguage = "ru";

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
            Log.e("MENU_ACTIVIT Y", "Firestore initialization failed");
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
            } else if(currentLanguage.equals("ru")) {
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
            else if(currentLanguage.equals("hy")){
                htmlText = "<h2><font color=\"#2E7D32\"><b>Ֆիտոֆտորոզ (Late blight)</b></font></h2><br/>\n" +
                        "<h3>Հիվանդության հարուցիչը</h3>" +
                        "<img src='fungal_reino_protista'/><br/>" +
                        "<p style='font-size: 20px; font-weight: 900; text-align: center;'><i>Phytophthora infestans</i> (օօմիցետ)</p>" +
                        "<h3>Տեր (վարակի ենթակա) բույսերը</h3>" +
                        "<p>Լոլիկ, տաքդեղ, սմբուկ, կարտոֆիլ և այլ մորմազգի (Solanaceae) ընտանիքի ներկայացուցիչներ։</p><br/>" +
                        "<h3>Ախտանիշները</h3>" +
                        "<p><b>Տերևների վրա</b></p>" +
                        "⦁ <i>Վաղ փուլում․</i> <span>տերևների վերին մակերեսին առաջանում են փոքր, անկանոն ձևի, ջրիկ բծեր՝ բաց կանաչից մինչև մուգ կանաչ երանգներով։<br/>" +
                        "⦁ <i>Հիվանդության զարգացման փուլում․</i> բծերը արագ ընդարձակվում են, դառնում են շագանակագույն կամ մանուշակագույն-սև, իսկ դրանց շուրջ ձևավորվում է դեղնավուն (քլորոտիկ) գոտի։<br/>" +
                        "⦁ <i>Վերջնական փուլում․</i> տերևների ստորին մակերեսի ախտահարված հատվածների եզրերին հայտնվում է սպիտակ, փափուկ փառ՝ կազմված պաթոգենի սպորներից։</span>" +
                        "<div style='display:flex; justify-content:center;'>" +
                        "<img src='fungal_late_blight' width='500' height='200'/><br/>" +
                        "<img src='fungal_phytophthora' width='500' height='200'/><br/>" +
                        "<img src='fungal_phytophthora_infestans_late' width='500' height='200'/><br/>" +
                        "</div>" +
                        "<p><b>Ցողունների և տերևակոթունների վրա</b></p>" +
                        "<p>⦁ Առաջանում են երկարավուն, մուգ շագանակագույն կամ սև նեկրոտիկ ախտահարված հատվածներ։<br/>" +
                        "⦁ Բարձր խոնավության պայմաններում ախտահարված հյուսվածքների մակերեսին կարող է ձևավորվել պաթոգենի սպորները։<br/>" +
                        "⦁ Ախտահարված հատվածներում հյուսվածքները դառնում են փխրուն, ինչի հետևանքով բույսերը թուլանում և ծռվում են։</p>" +
                        "<div style='display:flex; justify-content:center;'>" +
                        "<img src='fungal_phytophthora_infestans_root' width='500' height='200'/><br/>" +
                        "<img src='fungal_late_blight_3' width='500' height='200'/><br/>" +
                        "</div>" +
                        "<p><b>Պտուղների վրա</b></p>" +
                        "<p>⦁ Ախտահարումը սովորաբար սկսվում է պտղի վերին մասից։<br/>" +
                        "⦁ Մակերեսին ձևավորվում են շագանակագույն, երբեմն մոխրագունավուն-կանաչավուն երանգով կարծր բծեր։<br/>" +
                        "⦁ Պտուղների հյուսվածքները ժամանակի ընթացքում ենթարկվում են երկրորդային վարակման և փտում։</p>" +
                        "<div style='display:flex; justify-content:center;'>" +
                        "<img src='fungal_tomato' width='500' height='200'/><br/>" +
                        "<img src='fungal_late_blight_2' width='500' height='200'/><br/>" +
                        "</div>" +
                        "<h3>Էպիդեմիոլոգիա և զարգացման ցիկլ</h3>" +
                        "<p><b>Վարակի աղբյուրները:</b><br/>" +
                        "⦁ լոլիկի, կարտոֆիլի և այլ մորմազգի մշակաբույսերի մնացորդներ,<br/>" +
                        "⦁ վարակված սերմային կարտոֆիլի պալարներ,<br/>" +
                        "⦁ մորմազգի ընտանիքին պատկանող մոլախոտեր (օր.՝ սև փասլեն, Solanum nigrum),<br/>" +
                        "⦁ հարևան տարածքներում աճող վարակված մշակաբույսեր։</p><br/>" +
                        "<p><b>Զարգացում և տարածում</b><br/><br/>" +
                        "⦁ Նախնական վարակումը տեղի է ունենում, երբ սպորները շփվում են խոնավ տերևների կամ ցողունների հետ։<br/>" +
                        "⦁ Բարձր խոնավության պայմաններում ախտահարված հյուսվածքների վրա զարգանում են սպորները, որոնք տարածվում են քամու, անձրևի կաթիլների, միջատների կամ մարդկանց, գործիքների և կենդանիների միջոցով։<br/>" +
                        "⦁ Վարակը շատ արագ է տարածվում․ նոր օջախներ կարող են առաջանալ վարակումից ընդամենը 2–3 օր անց։</p>" +
                        "<h4><b>Կլիմայական պայմաններ, որոնք նպաստում են հիվանդության զարգացմանը</b></h4>" +
                        "<p>⦁ Օպտիմալ ջերմաստիճան․ 10–25 °C (ակտիվ զարգացում՝ 15–20 °C),<br/>" +
                        "⦁ Օդի խոնավություն․ 75–80% բարձր կամ տերևների վրա երկարատև խոնավության պահպանություն (ցող, մառախուղ, անձրև),<br/>" +
                        "⦁ Օրվա և գիշերվա ջերմաստիճանի կտրուկ տատանումներ,<br/>" +
                        "⦁ Անձրևային ոռոգում և տնկման խտությունը, որոնք խոչընդոտում են օդափոխությունը։</p>" +
                        "<h4>Էպիդեմիոլոգիական առանձնահատկություններ</h4>" +
                        "<p>⦁ Բաց դաշտի պայմաններում հիվանդությունը զարգանում է օջախային ձևով, մինչդեռ ջերմատներում կարող է ստանալ էպիֆիտոտիկ բնույթ։<br/>" +
                        "⦁ Բարենպաստ պայմաններում հիվանդությունը կարող է ոչնչացնել լոլիկի բերքը 7–10 օրվա ընթացքում։</p>" +
                        "<h3>Պայքարի և կանխարգելման միջոցառումներ</h3>" +
                        "<h5>Ագրոտեխնիկական միջոցառումներ</h5>" +
                        "<p>⦁ Պահպանել ցանքաշրջանառությունը․ մորմազգի մշակաբույսերը նույն տեղը վերադարձնել ոչ շուտ, քան 3–4 տարի անց։<br/>" +
                        "⦁ Բերքահավաքից հետո ամբողջությամբ հեռացնել և ոչնչացնել բույսերի մնացորդները։<br/>" +
                        "⦁ Օգտագործել առողջ տնկանյութ (սերտիֆիկացված բույսեր և պալարներ)։<br/>" +
                        "⦁ Ապահովել լավ օդափոխություն ջերմատներում՝ խուսափելով խտացված տնկումներից։<br/>" +
                        "⦁ Ոռոգումը կատարել հիմնականում արմատային եղանակով՝ խուսափելով անձրևային ոռոգումից։<br/>" +
                        "⦁ Պահպանել օպտիմալ միկրոկլիմա՝ նվազեցնելով խոնավությունը և բարելավելով օդի շրջանառությունը։</p>" +
                        "<h3>Կենսաբանական մեթոդներ</h3>" +
                        "<p>⦁ Օգտագործել միկրոբիոլոգիական պատրաստուկներ՝ Trichoderma spp., Bacillus subtilis, որոնք կանխում են պաթոգենի զարգացումը։</p>" +
                        "<h4>Քիմիական մեթոդներ</h4>" +
                        "<p>⦁ Կոնտակտային ֆունգիցիդներ․ պղնձային պատրաստուկներ (Բորդոյի հեղուկ, պղնձի քլորօքսիդ, պղնձի հիդրօքսիդ)։<br/>" +
                        "⦁ Սիստեմային ֆունգիցիդներ․ ակտիվ նյութեր՝ մետալաքսիլ, մանդիպրոպամիդ, ցիմօքսանիլ, ցիազոֆամիդ և այլն։<br/>" +
                        "⦁ Պատրաստուկները կիրառել խիստ ըստ կանոնակարգի՝ պահպանելով սպասման ժամկետներն ու նյութերի ռոտացիան՝ դիմադրություն չձևավորելու նպատակով։</p>" +
                        "<h4>Ինտեգրված բույսերի պաշտպանություն</h4>" +
                        "<p>⦁ Համակցել ագրոտեխնիկական, կենսաբանական և քիմիական միջոցառումները։<br/>" +
                        "⦁ Կատարել պարբերական ֆիտոսանիտար մոնիտորինգ։<br/>" +
                        "⦁ Օգտագործել կանխատեսման մոդելներ՝ հիմք ընդունելով ջերմաստիճանը, խոնավությունը և տեղումները՝ մշակաբույսերի մշակման ժամկետները որոշելու համար։<br/>" +
                        "⦁ Քիմիական միջոցների կիրառումը նվազեցնել՝ առավելություն տալով կանխարգելիչ և կենսաբանական մեթոդներին։</p>";

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
        }

        if (category.equals("Bacterial")) {
            if (currentLanguage.equals("en")) {// не работает!!
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
            }
            else if(currentLanguage.equals("ru")) {
                htmlText = "<h2><font color=\"#2E7D32\"><b>Бактериальный рак томата (Bacterial canker of tomato)</b></font></h2><br/>\n" +
                        "<h3>Возбудитель</h3>" +
                        "<p>Clavibacter michiganensis subsp. Michiganensis (Cmm)" +
                        "Грамположительная бактерия, принадлежащая к классу Actinobacteria. Патоген образует" +
                        "клеточные цепочки, выделяет экзополисахариды, ферменты и токсины, которые" +
                        "разрушают ткани растения, способствуя развитию некрозов и увядания. Cmm способен" +
                        "проникать в сосудистую систему растения через раны на корнях, стеблях или после" +
                        "механических повреждений, что обеспечивает системное распространение инфекции.</p><br/>"+
                        "<img src='bacterial_picture1'/><br/>" +

                        "<h3>Симптомы</h3>" +
                        "<p><b>Листья:</b></p>" +
                        "<p>⦁ Часто наблюдается одностороннее увядание верхушечных листьев. При этом одна сторона листа увядает, в то время как другая сохраняет тургор, что приводит к искривлению листовой пластины<br/>" +

                        "⦁ Пузыревидные или волдыреподобные пятна на листьях. Данный симптом появляется при вторичном заражении в питомниках томатов.<br/>" +
                        "⦁ При локальном инфицировании общее пожелтение листьев, особенно старых.</p>" +
                        "<div style = 'display:flex; justify-content: center;'>" +
                        "<img src='bacterial_picture2' width='500' height='200' /><br/>" +
                        "<img src='bacterial_picture3' width='500' height='200' /><br/>" +
//                        "<img src='fungal_phytophthora_infestans_late' width='500' height='200' /><br/>" +
                        "</div>" +

                        "<p><b>Стебли и черешки:</b></p>" +
                        "<p>⦁ <i>При системном инфицировании происходит постепенное потемнение проводящих тканей ксилемы. При этом Cmm, проникая через естественные отверстия или раны, размножается в ксилеме и способствует закупориванию сосудов, тем самым вызывая увядание растений. Начальные признаки увядания растений томата, обычно появляются в средней части растений, а потом распространяются вверх и вниз по стеблю, вызывая увядание и гибель растения.</i><br/>" +
                        "⦁ <i>Образование язв на стебле с коричневыми и полыми сосудистыми пучками на\n" +
                        "поздних стадиях инфекции.</i><br/>" +


                        "<div style = 'display:flex; justify-content: center;'>" +
                        "<img src='bacterial_picture4' width='500' height='200' /><br/>" +
                        "<img src='bacterial_picture5' width='500' height='200' /><br/>" +
                        "<img src='bacterial_picture6' width='500' height='200' /><br/>" +
                        "<img src='bacterial_picture7' width='500' height='200' /><br/>" +
                        "</div>" +

                        "<p><b>Плоды:</b></p>" +
                        "<p>⦁ Птичий глаз; на плодах- некротические пятна диаметром 1–3 мм на эпидермисе плодов. Цвет пятен варьируется от белого (у незрелых плодов томата) до темно-бурого (у зрелых).<br/>" +
                        "⦁ Инфицированные плоды могут иметь сетчатую или мраморную текстуру.</p>" +

                        "<div style = 'display:flex; justify-content: center;'>" +
                        "<img src='bacterial_picture8' width='500' height='200' /><br/>" +
                        "<img src='bacterial_picture9' width='500' height='200' /><br/>" +
                        "<img src='bacterial_picture10' width='500' height='200' /><br/>" +
                        "</div>" +

                        "<h3>Эпидемиология и жизненный цикл</h3>" +
                        "<p><b>Источники инфекции:</b><br/>"+
                        "⦁ Заражённые семена томата;<br/>" +
                        "⦁ больные растения в теплицах и открытых посевах;<br/>" +
                        "⦁ механические повреждения и инструменты, заражённые патогеном,;<br/>" +

                        "<p><b>Развитие и распространение:</b><br/><br/>" +
                        "⦁ Бактерия проникает через раны или повреждения, устьица и быстро распространяется по сосудистой системе,<br/>" +
                        "⦁ в закрытом грунте патогены могут сохраняются в течение многих лет, вызывая вспышки заболеваний почти в каждом обороте,<br/>" +
                        "⦁ распространяется механически, через полив, семена и инструменты.</p>" +

                        "<h4><b>Климатические условия, способствующие развитию:</b></h4>" +
                        "<p>⦁ Оптимальная температура: 10–25 °C (с наиболее активным развитием при 15–20 °C).<br/>" +
                        "⦁ механические повреждения растений и загущённые посадки повышают риск распространения инфекции.<br/></p>" +

                        "<h4>Особенности эпидемиологии:</h4>" +
                        "<p>⦁ Оптимальная температура: 20–26 °C,<br/>" +
                        "⦁ В теплицах болезнь может быстро распространяться и поражать все растения,</p>" +
                        "⦁ на открытых посевах развитие очаговое, но при благоприятных условиях\n" +
                        "наблюдается сильное увядание и гибель растений."+

                        "<h3>Меры борьбы и профилактика</h3>" +
                        "<h5>Агротехнические меры:</h5>" +
                        "<p>⦁ Использование сертифицированных, здоровых семян и саженцев,<br/>" +
                        "⦁ уничтожение растительных остатков после уборки урожая,<br/>" +
                        "⦁ соблюдение севооборота: возврат томата на прежнее место не ранее чем через 3–4 года," +
                        "⦁ обработка инструментов и оборудования дезинфицирующими средствами,<br/>" +
                        "⦁ поддержание оптимального микроклимата: хорошая вентиляция, избегание загущённых посадок.</p>" +

                        "<h3>Биологические методы:</h3>" +
                        "<p>Применение биофунгицидов, таких как Фитолавин (биофунгицид на основефитобактериомицина, природного антибиотика, получаемого из актиномицета Streptomyces griseus), для профилактики бактериальных заболеваний томата, включая бактериальный рак. Эффективность против Cmm ограничена и чаще служит вспомогательной мерой в комплексе с агротехническими приёмами.</p>" +

                        "<h4>Химические методы:</h4>" +
                        "<p>⦁ Использование медьсодержащих препаратов (медный оксихлорид, бордосская смесь) и биофунгицидов в профилактических целях.<br/>" +
                        "⦁ Антибактериальные средства (стрептомицин, касугамицин, тетрациклин) применяются только при высокой угрозе заражения и с учётом действующих норм по безопасности.</p>" +

                        "<h4>Интегрированная защита растений:</h4>" +
                        "<p>⦁ Сочетание агротехнических, биологических и химических приёмов.<br/>" +
                        "⦁ Регулярный фитосанитарный мониторинг растений.<br/>" +
                        "⦁ Своевременная локализация и удаление поражённых растений для предотвращения распространения.<br/>";
            }
            else if(currentLanguage.equals("hy")){// не работает
                htmlText = "<h2><font color=\"#2E7D32\"><b>Ֆիտոֆտորոզ (Late blight)</b></font></h2><br/>\n" +
                        "<h3>Հիվանդության հարուցիչը</h3>" +
                        "<img src='fungal_reino_protista'/><br/>" +
                        "<p style='font-size: 20px; font-weight: 900; text-align: center;'><i>Phytophthora infestans</i> (օօմիցետ)</p>" +
                        "<h3>Տեր (վարակի ենթակա) բույսերը</h3>" +
                        "<p>Լոլիկ, տաքդեղ, սմբուկ, կարտոֆիլ և այլ մորմազգի (Solanaceae) ընտանիքի ներկայացուցիչներ։</p><br/>" +
                        "<h3>Ախտանիշները</h3>" +
                        "<p><b>Տերևների վրա</b></p>" +
                        "⦁ <i>Վաղ փուլում․</i> <span>տերևների վերին մակերեսին առաջանում են փոքր, անկանոն ձևի, ջրիկ բծեր՝ բաց կանաչից մինչև մուգ կանաչ երանգներով։<br/>" +
                        "⦁ <i>Հիվանդության զարգացման փուլում․</i> բծերը արագ ընդարձակվում են, դառնում են շագանակագույն կամ մանուշակագույն-սև, իսկ դրանց շուրջ ձևավորվում է դեղնավուն (քլորոտիկ) գոտի։<br/>" +
                        "⦁ <i>Վերջնական փուլում․</i> տերևների ստորին մակերեսի ախտահարված հատվածների եզրերին հայտնվում է սպիտակ, փափուկ փառ՝ կազմված պաթոգենի սպորներից։</span>" +
                        "<div style='display:flex; justify-content:center;'>" +
                        "<img src='fungal_late_blight' width='500' height='200'/><br/>" +
                        "<img src='fungal_phytophthora' width='500' height='200'/><br/>" +
                        "<img src='fungal_phytophthora_infestans_late' width='500' height='200'/><br/>" +
                        "</div>" +
                        "<p><b>Ցողունների և տերևակոթունների վրա</b></p>" +
                        "<p>⦁ Առաջանում են երկարավուն, մուգ շագանակագույն կամ սև նեկրոտիկ ախտահարված հատվածներ։<br/>" +
                        "⦁ Բարձր խոնավության պայմաններում ախտահարված հյուսվածքների մակերեսին կարող է ձևավորվել պաթոգենի սպորները։<br/>" +
                        "⦁ Ախտահարված հատվածներում հյուսվածքները դառնում են փխրուն, ինչի հետևանքով բույսերը թուլանում և ծռվում են։</p>" +
                        "<div style='display:flex; justify-content:center;'>" +
                        "<img src='fungal_phytophthora_infestans_root' width='500' height='200'/><br/>" +
                        "<img src='fungal_late_blight_3' width='500' height='200'/><br/>" +
                        "</div>" +
                        "<p><b>Պտուղների վրա</b></p>" +
                        "<p>⦁ Ախտահարումը սովորաբար սկսվում է պտղի վերին մասից։<br/>" +
                        "⦁ Մակերեսին ձևավորվում են շագանակագույն, երբեմն մոխրագունավուն-կանաչավուն երանգով կարծր բծեր։<br/>" +
                        "⦁ Պտուղների հյուսվածքները ժամանակի ընթացքում ենթարկվում են երկրորդային վարակման և փտում։</p>" +
                        "<div style='display:flex; justify-content:center;'>" +
                        "<img src='fungal_tomato' width='500' height='200'/><br/>" +
                        "<img src='fungal_late_blight_2' width='500' height='200'/><br/>" +
                        "</div>" +
                        "<h3>Էպիդեմիոլոգիա և զարգացման ցիկլ</h3>" +
                        "<p><b>Վարակի աղբյուրները:</b><br/>" +
                        "⦁ լոլիկի, կարտոֆիլի և այլ մորմազգի մշակաբույսերի մնացորդներ,<br/>" +
                        "⦁ վարակված սերմային կարտոֆիլի պալարներ,<br/>" +
                        "⦁ մորմազգի ընտանիքին պատկանող մոլախոտեր (օր.՝ սև փասլեն, Solanum nigrum),<br/>" +
                        "⦁ հարևան տարածքներում աճող վարակված մշակաբույսեր։</p><br/>" +
                        "<p><b>Զարգացում և տարածում</b><br/><br/>" +
                        "⦁ Նախնական վարակումը տեղի է ունենում, երբ սպորները շփվում են խոնավ տերևների կամ ցողունների հետ։<br/>" +
                        "⦁ Բարձր խոնավության պայմաններում ախտահարված հյուսվածքների վրա զարգանում են սպորները, որոնք տարածվում են քամու, անձրևի կաթիլների, միջատների կամ մարդկանց, գործիքների և կենդանիների միջոցով։<br/>" +
                        "⦁ Վարակը շատ արագ է տարածվում․ նոր օջախներ կարող են առաջանալ վարակումից ընդամենը 2–3 օր անց։</p>" +
                        "<h4><b>Կլիմայական պայմաններ, որոնք նպաստում են հիվանդության զարգացմանը</b></h4>" +
                        "<p>⦁ Օպտիմալ ջերմաստիճան․ 10–25 °C (ակտիվ զարգացում՝ 15–20 °C),<br/>" +
                        "⦁ Օդի խոնավություն․ 75–80% բարձր կամ տերևների վրա երկարատև խոնավության պահպանություն (ցող, մառախուղ, անձրև),<br/>" +
                        "⦁ Օրվա և գիշերվա ջերմաստիճանի կտրուկ տատանումներ,<br/>" +
                        "⦁ Անձրևային ոռոգում և տնկման խտությունը, որոնք խոչընդոտում են օդափոխությունը։</p>" +
                        "<h4>Էպիդեմիոլոգիական առանձնահատկություններ</h4>" +
                        "<p>⦁ Բաց դաշտի պայմաններում հիվանդությունը զարգանում է օջախային ձևով, մինչդեռ ջերմատներում կարող է ստանալ էպիֆիտոտիկ բնույթ։<br/>" +
                        "⦁ Բարենպաստ պայմաններում հիվանդությունը կարող է ոչնչացնել լոլիկի բերքը 7–10 օրվա ընթացքում։</p>" +
                        "<h3>Պայքարի և կանխարգելման միջոցառումներ</h3>" +
                        "<h5>Ագրոտեխնիկական միջոցառումներ</h5>" +
                        "<p>⦁ Պահպանել ցանքաշրջանառությունը․ մորմազգի մշակաբույսերը նույն տեղը վերադարձնել ոչ շուտ, քան 3–4 տարի անց։<br/>" +
                        "⦁ Բերքահավաքից հետո ամբողջությամբ հեռացնել և ոչնչացնել բույսերի մնացորդները։<br/>" +
                        "⦁ Օգտագործել առողջ տնկանյութ (սերտիֆիկացված բույսեր և պալարներ)։<br/>" +
                        "⦁ Ապահովել լավ օդափոխություն ջերմատներում՝ խուսափելով խտացված տնկումներից։<br/>" +
                        "⦁ Ոռոգումը կատարել հիմնականում արմատային եղանակով՝ խուսափելով անձրևային ոռոգումից։<br/>" +
                        "⦁ Պահպանել օպտիմալ միկրոկլիմա՝ նվազեցնելով խոնավությունը և բարելավելով օդի շրջանառությունը։</p>" +
                        "<h3>Կենսաբանական մեթոդներ</h3>" +
                        "<p>⦁ Օգտագործել միկրոբիոլոգիական պատրաստուկներ՝ Trichoderma spp., Bacillus subtilis, որոնք կանխում են պաթոգենի զարգացումը։</p>" +
                        "<h4>Քիմիական մեթոդներ</h4>" +
                        "<p>⦁ Կոնտակտային ֆունգիցիդներ․ պղնձային պատրաստուկներ (Բորդոյի հեղուկ, պղնձի քլորօքսիդ, պղնձի հիդրօքսիդ)։<br/>" +
                        "⦁ Սիստեմային ֆունգիցիդներ․ ակտիվ նյութեր՝ մետալաքսիլ, մանդիպրոպամիդ, ցիմօքսանիլ, ցիազոֆամիդ և այլն։<br/>" +
                        "⦁ Պատրաստուկները կիրառել խիստ ըստ կանոնակարգի՝ պահպանելով սպասման ժամկետներն ու նյութերի ռոտացիան՝ դիմադրություն չձևավորելու նպատակով։</p>" +
                        "<h4>Ինտեգրված բույսերի պաշտպանություն</h4>" +
                        "<p>⦁ Համակցել ագրոտեխնիկական, կենսաբանական և քիմիական միջոցառումները։<br/>" +
                        "⦁ Կատարել պարբերական ֆիտոսանիտար մոնիտորինգ։<br/>" +
                        "⦁ Օգտագործել կանխատեսման մոդելներ՝ հիմք ընդունելով ջերմաստիճանը, խոնավությունը և տեղումները՝ մշակաբույսերի մշակման ժամկետները որոշելու համար։<br/>" +
                        "⦁ Քիմիական միջոցների կիրառումը նվազեցնել՝ առավելություն տալով կանխարգելիչ և կենսաբանական մեթոդներին։</p>";

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
                        int originalWidth = drawable.getIntrinsicWidth();
                        int originalHeight = drawable.getIntrinsicHeight();

                        int maxWidth = 1000;
                        int newHeight = (originalHeight * maxWidth) / originalWidth;

                        int left = (screenWidth - maxWidth) / 2;
                        int right = (screenWidth + maxWidth) / 2;

                        drawable.setBounds(left, 0, right, newHeight);
                    }

                    return drawable;
                }
            }, null));
        }

        if (category.equals("Viral")) {
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
            }
            else if(currentLanguage.equals("ru")) {
                htmlText = "<h2><font color=\"#2E7D32\"><b>Вирус мозаики пепино (Pepino mosaic virus)b></font></h2><br/>\n" +
                        "<h3>Возбудитель</h3>" +
                        "<p><i>Pepino mosaic virus (PepMV)</i> <br/>Вирус из рода <i>Potexvirus</i>, семейство <i>Alphaflexiviridae</i>. РНК-содержащий вирус с положительной одноцепочечной РНК. PepMV может обнаруживаться практически во всех надземных и подземных частях растения, заражённого примерно за 4 недели до проявления симптомов. Симптоматика зависит от сорта томата, условий выращивания и штамма вируса.</p><br/>"+
                        "<img src='virus_picture1'/><br/>" +
                        "<h3>Круг хозяев</h3>"+
                        "<p>Томат (основной хозяин), редко заражает баклажан, перец и паслёновые декоративные\n" +
                        "растения.<p><br/>"+

                        "<h3>Симптомы</h3>" +
                        "<p><b>Листья:</b></p>" +
                        "<p>⦁ Появление мозаичных или пятнистых желтовато-зелёных участков,<br/>" +
                        "⦁ общее пожелтение и некроз листьев,<br/>" +
                        "⦁ появление волдыревидных участков на поверхности листа.</p>" +
                        "<div style = 'display:flex; justify-content: center;'>" +
                        "<img src='virus_picture2' width='500' height='200' /><br/>" +
                        "<img src='virus_picture3' width='500' height='200' /><br/>" +
                        "<img src='virus_picture4' width='500' height='200' /><br/>" +
                        "<img src='virus_picture5' width='500' height='200' /><br/>" +
                        "</div>" +

                        "<p><b>Стебли и черешки:</b></p>" +
                        "<p>⦁ Появление коричневых или корковых полос на стеблях. <br/>" +

                        "<p><b>Плоды:</b></p>" +

                        "<p>⦁Наиболее типичные и экономически значимые симптомы — изменение окраски плодов: мраморный рисунок, полосы разных оттенков, пятнистость.<br/>" +
                        "⦁ В отдельных случаях — трещины, деформация и неправильная форма плодов." +
                        "⦁ Плоды созревают неравномерно, снижая их качество и товарность.</p>" +

                        "<div style = 'display:flex; justify-content: center;'>" +
                        "<img src='bacterial_picture6' width='500' height='200' /><br/>" +
                        "<img src='bacterial_picture7' width='500' height='200' /><br/>" +
                        "</div>" +

                        "<h3>Эпидемиология и жизненный цикл</h3>" +
                        "<p>⦁ Заражённые семена томата и рассадный материал,<br/>" +
                        "⦁ контакт инфицированными растениями через инструменты и механические повреждения,<br/>" +
                        "⦁ механические повреждения и инструменты, заражённые патогеном,;<br/>" +
                        "⦁ пчёлы и другие насекомые могут способствовать механическому переносу вируса.</p>"+
                        "<p><b>Развитие и распространение:</b><br/><br/>" +
                        "⦁ Вирус проникает в растение через микроповреждения и быстро распространяется по клеткам,<br/>" +
                        "⦁ в теплицах PepMV может быстро распространяться через тесный контакт и механическое воздействие при уходе за растениями,<br/>" +
                        "⦁ на открытых посевах скорость распространения ниже, но вирус сохраняется в заражённых растениях и семенах.</p>" +

                        "<h4><b>Климатические условия, способствующие развитию:</b></h4>" +
                        "<p>⦁ Любая температура, оптимальная для роста томата, способствует распространению\n" +
                        "вируса,<br/>" +
                        "⦁ Высокая плотность посадок и частые манипуляции с растениями ускоряют\n" +
                        "передачу вируса.<br/></p>" +

                        "<h4>Особенности эпидемиологии:</h4>" +
                        "<p>⦁ В теплицах вирус может вызывать эпифитотические вспышки.<br/>" +
                        "⦁ Вирус устойчив к хранению на поверхностях и инструментах, что делает механические меры контроля критически важными.</p>"+

                        "<h3>Меры борьбы и профилактика</h3>" +
                        "<h5>Агротехнические меры:</h5>" +
                        "<p>⦁ Использование сертифицированных, здоровых семян и рассадного материала,<br/>" +
                        "⦁ дезинфекция инструментов, рук и оборудования при уходе за растениями,<br/>" +
                        "⦁ поддержание оптимального микроклимата и избегание загущённых посадок," +
                        "⦁ удаление и уничтожение инфицированных растений для предотвращения распространения.<br/></p>" +

                        "<h3>Биологические методы:</h3>" +
                        "<p>⦁ На сегодняшний день эффективные биологические препараты для контроля PepMV отсутствуют." +
                        "⦁ Используются устойчивые сорта томата, а также профилактические меры для снижения механического переноса вируса.</p>" +

                        "<h4>Химические методы:</h4>" +
                        "<p>⦁ Прямых химических средств против PepMV не существует, так как вирусы не поддаются обработке фунгицидами или антибиотиками.<br/>" +
                        "⦁ Профилактически применяют средства для дезинфекции инструментов и оборудования.</p>" +

                        "<h4>Интегрированная защита растений:</h4>" +
                        "<p>⦁ Сочетание агротехнических мер, санитарного контроля и использования здорового посадочного материала.<br/>" +
                        "⦁ Регулярный мониторинг растений для своевременного выявления симптомов вируса.<br/>" +
                        "⦁ Изоляция и уничтожение инфицированных растений для предотвращения распространения вируса.<br/>";
            }
            else if(currentLanguage.equals("hy")){
                htmlText = "<h2><font color='#2E7D32'><b>Պեպինո մոզաիկային վիրուս (Pepino mosaic virus)</b></font></h2><br/>" +
                        "<h3>Հիվանդության հարուցիչը</h3>" +
                        "<p><i>Pepino mosaic virus (PepMV)</i><br/>Վիրուսը պատկանում է <i>Potexvirus</i> ցեղին և <i>Alphaflexiviridae</i> ընտանիքին։ Այն դրական միաշղթայական ՌՆԹ-ով վիրուս է, որը կարող է հայտնաբերվել բույսի գրեթե բոլոր վերին և ստորին մասերում՝ վարակումից մոտ 4 շաբաթ անց մինչև ախտանիշների ի հայտ գալը։ Ախտանիշները կախված են լոլիկի սորտից, աճեցման պայմաններից և վիրուսի շտամից։</p><br/>" +
                        "<img src='virus_picture1'/><br/>" +

                        "<h3>Տեր (վարակի ենթակա) բույսերը</h3>" +
                        "<p>Հիմնականում լոլիկ, հազվադեպ սմբուկ, տաքդեղ և մորմազգի դեկորատիվ բույսեր։</p><br/>" +

                        "<h3>Ախտանիշները</h3>" +
                        "<p><b>Տերևներ․</b></p>" +
                        "<p>⦁ Մոզաիկային կամ դեղնաչա-կանաչ բծավոր հատվածների առաջացում,<br/>" +
                        "⦁ Տերևների ընդհանուր դեղնում և նեկրոզ,<br/>" +
                        "⦁ Մակերեսի վրա բլիթավոր հատվածների առաջացում։</p>" +
                        "<div style='display:flex; justify-content:center;'>" +
                        "<img src='virus_picture2' width='500' height='200'/><br/>" +
                        "<img src='virus_picture3' width='500' height='200'/><br/>" +
                        "<img src='virus_picture4' width='500' height='200'/><br/>" +
                        "<img src='virus_picture5' width='500' height='200'/><br/>" +
                        "</div>" +

                        "<p><b>Ցողուններ և քթամաններ․</b></p>" +
                        "<p>⦁ Շագանակագույն կամ կեղևանման գծերի առաջացում ցողունների վրա։</p>" +

                        "<p><b>Պտուղներ․</b></p>" +
                        "<p>⦁ Պտղի գույնի փոփոխություն՝ մարմարային պատկեր, գծավորություն, բծավորություն,<br/>" +
                        "⦁ Հազվադեպ՝ ճեղքեր, դեֆորմացիա և ձևի շեղում,<br/>" +
                        "⦁ Հասունացումը անհամաչափ է, ինչն իջեցնում է որակը և առևտրային արժեքը։</p>" +

                        "<div style='display:flex; justify-content:center;'>" +
                        "<img src='bacterial_picture6' width='500' height='200'/><br/>" +
                        "<img src='bacterial_picture7' width='500' height='200'/><br/>" +
                        "</div>" +

                        "<h3>Էպիդեմիոլոգիա և զարգացման ցիկլ</h3>" +
                        "<p>⦁ Վարակված լոլիկի սերմեր և տնկանյութ,<br/>" +
                        "⦁ Կոնտակտային վարակ՝ գործիքների և մեխանիկական վնասվածքների միջոցով,<br/>" +
                        "⦁ Մեխանիկական փոխանցում վարակված մակերեսների միջոցով,<br/>" +
                        "⦁ Մեղուներն ու այլ միջատներ կարող են նպաստել վիրուսի տարածմանը։</p>" +

                        "<p><b>Զարգացում և տարածում․</b><br/><br/>" +
                        "⦁ Վիրուսը ներթափանցում է միկրովնասվածքների միջոցով և արագ տարածվում բջիջների միջև,<br/>" +
                        "⦁ Ջերմատներում արագ տարածվում է բույսերի խնամքի ընթացքում,<br/>" +
                        "⦁ Բաց դաշտում տարածումը դանդաղ է, բայց վիրուսը պահպանվում է բույսերում և սերմերում։</p>" +

                        "<h4><b>Կլիմայական պայմաններ, որոնք նպաստում են զարգացմանը․</b></h4>" +
                        "<p>⦁ 10–25°C միջակայքում (ակտիվ՝ 15–20°C),<br/>" +
                        "⦁ Բարձր խոնավություն, երկարատև ցող կամ մառախուղ,<br/>" +
                        "⦁ Ջերմաստիճանի կտրուկ տատանումներ,<br/>" +
                        "⦁ Խիտ տնկումներ և թույլ օդափոխություն։</p>" +

                        "<h4>Էպիդեմիոլոգիական առանձնահատկություններ</h4>" +
                        "<p>⦁ Բաց դաշտում զարգանում է օջախային կերպով,<br/>" +
                        "⦁ Ջերմատներում կարող է դառնալ էպիֆիտոտիկ,<br/>" +
                        "⦁ Նպաստավոր պայմաններում կարող է լրջորեն վնասել բերքը ընդամենը 7–10 օրում։</p>" +

                        "<h3>Պայքարի և կանխարգելման միջոցառումներ</h3>" +
                        "<h5>Ագրոտեխնիկական միջոցներ</h5>" +
                        "<p>⦁ Պահպանել ցանքաշրջանառությունը (մորմազգին – ոչ շուտ քան 3–4 տարի անց),<br/>" +
                        "⦁ Հեռացնել և ոչնչացնել բուսական մնացորդները,<br/>" +
                        "⦁ Օգտագործել առողջ, սերտիֆիկացված տնկանյութ,<br/>" +
                        "⦁ Ջերմատներում ապահովել լավ օդափոխություն և խուսափել խիտ տնկումներից,<br/>" +
                        "⦁ Կատարել կաթիլային ոռոգում,<br/>" +
                        "⦁ Կարգավորել միկրոկլիման՝ նվազեցնելով խոնավությունը։</p>" +

                        "<h3>Կենսաբանական մեթոդներ</h3>" +
                        "<p>⦁ Օգտագործել կենսաբանական պատրաստուկներ՝ Trichoderma spp., Bacillus subtilis և այլն։</p>" +

                        "<h4>Քիմիական մեթոդներ</h4>" +
                        "<p>⦁ Օգտագործվում են պղնձային կոնտակտային ֆունգիցիդներ (Բորդոյան հեղուկ, պղնձի օքսիքլորիդ, պղնձի հիդրօքսիդ),<br/>" +
                        "⦁ Համակարգային ֆունգիցիդներ՝ մետալաքսիլ, մեբենոքսամ, մանդիպրոպամիդ, ցիմոքսանիլ, ցիազոֆամիդ և այլն։</p>" +

                        "<h4>Ինտեգրված պաշտպանություն</h4>" +
                        "<p>⦁ Ագրոտեխնիկական, կենսաբանական և քիմիական միջոցների համակցված կիրառում,<br/>" +
                        "⦁ Տարածքների պարբերական ֆիտոսանիտարական մոնիթորինգ,<br/>" +
                        "⦁ Կանխատեսման մոդելների օգտագործում (ջերմաստիճան, խոնավություն, տեղումներ),<br/>" +
                        "⦁ Քիմիական միջոցների նվազում՝ նախապատվությունը տալով կանխարգելիչ մեթոդներին։</p>";;

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
                        int originalWidth = drawable.getIntrinsicWidth();
                        int originalHeight = drawable.getIntrinsicHeight();

                        int maxWidth = 1000;
                        int newHeight = (originalHeight * maxWidth) / originalWidth;

                        int left = (screenWidth - maxWidth) / 2;
                        int right = (screenWidth + maxWidth) / 2;

                        drawable.setBounds(left, 0, right, newHeight);
                    }

                    return drawable;
                }
            }, null));
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