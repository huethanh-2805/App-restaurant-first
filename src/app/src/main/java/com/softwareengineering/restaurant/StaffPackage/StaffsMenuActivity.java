package com.softwareengineering.restaurant.StaffPackage;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softwareengineering.restaurant.CustomerPackage.FoodAdapter;
import com.softwareengineering.restaurant.ItemClasses.Food;
import com.softwareengineering.restaurant.LoginActivity;
import com.softwareengineering.restaurant.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaffsMenuActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private ImageView topMenuImg;
    private CircleImageView userAvatar;
    private TextView topMenuName, userName;
    private RelativeLayout customers, menu, tables, reports, payment, account, logout;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private List<Food> foodList = new ArrayList<>();
    private FoodAdapter foodAdapter;
    private List<Food> foodListHolder = new ArrayList<>();
    private GridView gridView;
    private EditText searchMenu;
    private LinearLayout saladButton, pizzaButton, drinkButton, dessertButton, pastaButton, burgerButton, otherButton;

    private TextView statusClick;

    private FILTER_TYPE g1_filterType = FILTER_TYPE.FULL;

    public enum FILTER_TYPE {
        FULL,
        SALAD,
        PASTA,
        PIZZA,
        DESSERT,
        DRINK,
        BURGER,
        OTHERS
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staffs_menu);

        mAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.staffsDrawerLayout);
        topMenuImg = findViewById(R.id.topMenuImg);
        topMenuName = findViewById(R.id.topMenuName);
        customers = findViewById(R.id.staffsCustomersDrawer);
        menu = findViewById(R.id.staffsMenuDrawer);
        tables = findViewById(R.id.staffsTablesDrawer);
        reports = findViewById(R.id.staffsReportsDrawer);
        payment = findViewById(R.id.staffsPaymentDrawer);
        account = findViewById(R.id.staffsAccountDrawer);
        logout = findViewById(R.id.staffsLogoutDrawer);
        userAvatar = findViewById(R.id.staffsNavAvatar);
        userName = findViewById(R.id.staffsNavName);
        gridView = findViewById(R.id.list_menu);
        searchMenu = findViewById(R.id.search_menu);

        // Filter button setup here:
        saladButton = findViewById(R.id.saladFilter);
        drinkButton = findViewById(R.id.drinkFilter);
        dessertButton = findViewById(R.id.dessertFilter);
        pizzaButton = findViewById(R.id.pizzaFilter);
        pastaButton = findViewById(R.id.pastaFilter);
        burgerButton = findViewById(R.id.burgerFilter);
        otherButton = findViewById(R.id.otherFilter);

        //always showing by foodListHolder
        foodAdapter = new FoodAdapter(this, foodList);
        gridView.setAdapter(foodAdapter);

        //Synchronize by event listener
        realtimeUpdateMenu();

        // Initialize Current User
        initCurrentUser();
        initToolBar();
        initNavBar();

        // Handle search menu
        handleSearchMenu();

        //filter click
        saladButton.setOnClickListener(saladClickEvent);
        drinkButton.setOnClickListener(drinkClickEvent);
        dessertButton.setOnClickListener(dessertClickEvent);
        burgerButton.setOnClickListener(burgerClickEvent);
        pizzaButton.setOnClickListener(pizzaClickEvent);
        pastaButton.setOnClickListener(pastaClickEvent);
        otherButton.setOnClickListener(otherClickEvent);

        //statusClick.setOnClickListener(statusChangeEvent);
    }

    private void handleSearchMenu() {
        searchMenu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchSearchedFoodList(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initNavBar() {
        setItemBackgroundColors(menu);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(menu);
                closeDrawer(drawerLayout);
            }
        });

        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(customers);
                redirectActivity(StaffsMenuActivity.this, StaffsCustomersActivity.class);
            }
        });

        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(tables);
                redirectActivity(StaffsMenuActivity.this, StaffsTablesActivity.class);
            }
        });

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(reports);
                redirectActivity(StaffsMenuActivity.this, StaffsReportsActivity.class);
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(payment);
                redirectActivity(StaffsMenuActivity.this, StaffsPaymentActivity.class);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemBackgroundColors(account);
                redirectActivity(StaffsMenuActivity.this, StaffsAccountActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectActivity(StaffsMenuActivity.this, LoginActivity.class);
            }
        });
    }

    private void initToolBar() {
        topMenuImg.setImageResource(R.drawable.topmenu);

        topMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        topMenuName.setText(R.string.menu);
    }

    private void initCurrentUser() {
        // Get currentUser
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        Uri avatarPhotoUrl = currentUser.getPhotoUrl();
        // Avatar Image
        Picasso.get().load(avatarPhotoUrl).placeholder(R.drawable.default_user).into(userAvatar);
        // Get user info from firestore
        getUserInfoFirestore(currentUser.getUid());
    }

    private void getUserInfoFirestore(String uid) {
        DocumentReference userRef = firestore.collection("users").document(uid);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Get user info
                    String name;
                    name = document.getString("name");

                    // Set user info
                    userName.setText(name);

                } else {
                    // User document not found
                    Log.d("Auth Firestore Database", "No such document");
                }
            } else {
                Log.d("Auth Firestore Database", "get failed with ", task.getException());
            }
        });

    }

    private void fetchFoodList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference foodCollection = db.collection("food");
        foodList.clear();

        foodCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        if (doc.getBoolean("state") == null)
                            Log.d("null_Long", doc.getId().toString());
                        Food food = new Food(
                                doc.getString("imageRef"),
                                doc.getString("imageURL"),
                                doc.getString("name"),
                                doc.getLong("price"),
                                Boolean.TRUE.equals(doc.getBoolean("state")),
                                doc.getString("type")
                        );
                        foodList.add(food);
                    }
                    foodListHolder.clear();
                    //changed UI
                    foodAdapter.notifyDataSetChanged();
                    foodListHolder.addAll(foodList);
                } else {
                    Log.e("CustomersMenuActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void fetchSearchedFoodList(String searchedName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference foodCollection = db.collection("food");
        foodList.clear();

        foodCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        if (doc.getBoolean("state") == null)
                            Log.d("null_Long", doc.getId().toString());
                        Food food = new Food(
                                doc.getString("imageRef"),
                                doc.getString("imageURL"),
                                doc.getString("name"),
                                doc.getLong("price"),
                                Boolean.TRUE.equals(doc.getBoolean("state")),
                                doc.getString("type")
                        );

                        if (food.getName().toLowerCase().contains(searchedName)
                                || food.getName().toLowerCase().equals(searchedName)) {
                            foodList.add(food);
                        }
                    }
                    foodListHolder.clear();
                    //changed UI
                    foodAdapter.notifyDataSetChanged();
                    foodListHolder.addAll(foodList);
                } else {
                    Log.e("CustomersMenuActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void setItemBackgroundColors(RelativeLayout selectedItem) {
        customers.setBackgroundColor(selectedItem == customers ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
        menu.setBackgroundColor(selectedItem == menu ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
        tables.setBackgroundColor(selectedItem == tables ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
        reports.setBackgroundColor(selectedItem == reports ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
        payment.setBackgroundColor(selectedItem == payment ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
        account.setBackgroundColor(selectedItem == account ? ContextCompat.getColor(this, R.color.light_orange_3) : ContextCompat.getColor(this, R.color.light_orange_2));
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    View.OnClickListener saladClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (g1_filterType == FILTER_TYPE.SALAD) {
                g1_filterType = FILTER_TYPE.FULL;
                fetchFoodList();
                deselectFilter(saladButton);
                return;
            }
            g1_filterType = FILTER_TYPE.SALAD;
            filterClickedShowing("Salad");
            changeToggleColor(saladButton);
        }
    };

    View.OnClickListener drinkClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (g1_filterType == FILTER_TYPE.DRINK) {
                g1_filterType = FILTER_TYPE.FULL;
                fetchFoodList();
                deselectFilter(drinkButton);
                return;
            }
            g1_filterType = FILTER_TYPE.DRINK;
            filterClickedShowing("Drink");
            changeToggleColor(drinkButton);
        }
    };

    View.OnClickListener dessertClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (g1_filterType == FILTER_TYPE.DESSERT) {
                g1_filterType = FILTER_TYPE.FULL;
                fetchFoodList();
                deselectFilter(dessertButton);
                return;
            }
            g1_filterType = FILTER_TYPE.DESSERT;
            filterClickedShowing("Dessert");
            changeToggleColor(dessertButton);
        }
    };

    View.OnClickListener pastaClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (g1_filterType == FILTER_TYPE.PASTA) {
                g1_filterType = FILTER_TYPE.FULL;
                fetchFoodList();
                deselectFilter(pastaButton);
                return;
            }
            g1_filterType = FILTER_TYPE.PASTA;
            filterClickedShowing("Pasta");
            changeToggleColor(pastaButton);
        }
    };

    View.OnClickListener burgerClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (g1_filterType == FILTER_TYPE.BURGER) {
                g1_filterType = FILTER_TYPE.FULL;
                fetchFoodList();
                deselectFilter(burgerButton);
                return;
            }
            g1_filterType = FILTER_TYPE.BURGER;
            filterClickedShowing("Burger");
            changeToggleColor(burgerButton);
        }
    };

    View.OnClickListener pizzaClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (g1_filterType == FILTER_TYPE.PIZZA) {
                g1_filterType = FILTER_TYPE.FULL;
                fetchFoodList();
                deselectFilter(pizzaButton);
                return;
            }
            g1_filterType = FILTER_TYPE.PIZZA;
            filterClickedShowing("Pizza");
            changeToggleColor(pizzaButton);
        }
    };

    View.OnClickListener otherClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (g1_filterType == FILTER_TYPE.OTHERS) {
                g1_filterType = FILTER_TYPE.FULL;
                fetchFoodList();
                deselectFilter(otherButton);
                return;
            }
            g1_filterType = FILTER_TYPE.OTHERS;
            filterClickedShowing("Other");
            changeToggleColor(otherButton);
        }
    };

    private final String TAG = "UserChecker";

    //Filter handler:
    private void filterClickedShowing(String filterValue) {
        if (filterValue != "Other") {
            //Known that foodList is fetched successfully
            ArrayList<Food> foodFilter = foodListHolder.stream()
                    .filter(x -> x.getType().equals(filterValue))
                    .collect(Collectors.toCollection(ArrayList::new));

            foodAdapter.updateData(foodFilter);
            foodAdapter.notifyDataSetChanged();
        } else {
            String[] basicType = {"Salad", "Pasta", "Drink", "Dessert", "Pizza", "Burger"};

            ArrayList<Food> foodFilter = foodListHolder.stream()
                    .filter(x -> !Arrays.asList(basicType).contains(x.getType()))
                    .collect(Collectors.toCollection(ArrayList::new));

            foodAdapter.updateData(foodFilter);
            foodAdapter.notifyDataSetChanged();
        }
    }

    private void realtimeUpdateMenu() {
        firestore.collection("food").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()) {
                    foodListHolder.clear();
                    fetchFoodList(); //FIXME: i can't find out where's foodListHolder value after calling the second level callback
                    Log.d("Drink_reach", String.valueOf(foodList.size()));

                    // Handle filters
                    switch (g1_filterType) {
                        case DRINK: {
                            g1_filterType = FILTER_TYPE.FULL;
                            deselectFilter(drinkButton);
                            break;
                        }
                        case PASTA: {
                            g1_filterType = FILTER_TYPE.FULL;
                            deselectFilter(pastaButton);
                            break;
                        }
                        case SALAD: {
                            g1_filterType = FILTER_TYPE.FULL;
                            deselectFilter(saladButton);
                            break;
                        }
                        case PIZZA: {
                            g1_filterType = FILTER_TYPE.FULL;
                            deselectFilter(pizzaButton);
                            break;
                        }
                        case DESSERT: {
                            g1_filterType = FILTER_TYPE.FULL;
                            deselectFilter(dessertButton);
                            break;
                        }
                        case BURGER: {
                            g1_filterType = FILTER_TYPE.FULL;
                            deselectFilter(burgerButton);
                            break;
                        }
                        case OTHERS: {
                            g1_filterType = FILTER_TYPE.FULL;
                            deselectFilter(otherButton);
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        });
    }

    private void changeToggleColor(LinearLayout selectedFilter) {
        int selectedColor = getResources().getColor(R.color.orange);
        int deselectedColor = getResources().getColor(R.color.white);

        saladButton.setBackgroundTintList(ColorStateList.valueOf(selectedFilter == saladButton ? selectedColor : deselectedColor));
        drinkButton.setBackgroundTintList(ColorStateList.valueOf(selectedFilter == drinkButton ? selectedColor : deselectedColor));
        pizzaButton.setBackgroundTintList(ColorStateList.valueOf(selectedFilter == pizzaButton ? selectedColor : deselectedColor));
        dessertButton.setBackgroundTintList(ColorStateList.valueOf(selectedFilter == dessertButton ? selectedColor : deselectedColor));
        pastaButton.setBackgroundTintList(ColorStateList.valueOf(selectedFilter == pastaButton ? selectedColor : deselectedColor));
        burgerButton.setBackgroundTintList(ColorStateList.valueOf(selectedFilter == burgerButton ? selectedColor : deselectedColor));
        otherButton.setBackgroundTintList(ColorStateList.valueOf(selectedFilter == otherButton ? selectedColor : deselectedColor));
    }

    private void deselectFilter(LinearLayout selectedFilter) {
        int deselectedColor = getResources().getColor(R.color.white);
        selectedFilter.setBackgroundTintList(ColorStateList.valueOf(deselectedColor));
    }


}