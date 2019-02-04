package com.jiafrank.keepreceipt.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;
import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.service.TextFormatService;

import java.util.Calendar;
import java.util.Locale;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import static com.jiafrank.keepreceipt.Constants.PICK_CATEGORY;
import static com.jiafrank.keepreceipt.Constants.SELECTED_CATEGORY_INTENT_NAME;

public class SearchFragment extends Fragment {

    // UI references
    private View rootView;
    private Activity parentActivity;
    private ActionBar actionBar;
    private TextInputEditText keywordsInput;
    private TextInputEditText categoriesInput;
    private TextInputEditText maxPriceInput;
    private TextInputEditText minPriceInput;
    private TextInputEditText maxDateInput;
    private TextInputEditText minDateInput;
    private ImageButton clearMaxDateInputButton;
    private ImageButton clearMinDateInputButton;
    private Button searchButton;

    // State Variables
    private Calendar nowCalendar = Calendar.getInstance(Locale.getDefault());
    private Calendar maxStatedCalendar;
    private Calendar minStatedCalendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        parentActivity = getActivity();

        // Initialize views
        actionBar = ((HomeActivity) parentActivity).getSupportActionBar();
        keywordsInput = rootView.findViewById(R.id.searchKeywordsInput);
        categoriesInput = rootView.findViewById(R.id.searchCategoryInput);
        maxPriceInput = rootView.findViewById(R.id.searchMaxPriceInput);
        minPriceInput = rootView.findViewById(R.id.searchMinPriceInput);
        maxDateInput = rootView.findViewById(R.id.searchMaxDateInput);
        minDateInput = rootView.findViewById(R.id.searchMinDateInput);
        clearMaxDateInputButton = rootView.findViewById(R.id.maxDateClearButton);
        clearMinDateInputButton = rootView.findViewById(R.id.minDateClearButton);
        searchButton = rootView.findViewById(R.id.searchButton);

        setUpUI();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void setUpUI() {

        // Set new title
        actionBar.setTitle(getString(R.string.search_title));

        /**
         * Date Inputs
         */
        // First hide the clear date buttons
        showOrHideClearDateButtons();
        // MAX DATE
        // Set clear button listener
        clearMaxDateInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxStatedCalendar = null;
                maxDateInput.setText(null);
                showOrHideClearDateButtons();
            }
        });
        // Set listener for setting date
        final DatePickerDialog.OnDateSetListener onMaxDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (maxStatedCalendar == null) {
                    maxStatedCalendar = Calendar.getInstance(Locale.getDefault());
                }
                maxStatedCalendar.set(year, month, dayOfMonth);
                maxDateInput.setText(TextFormatService.getFormattedStringFromDate(maxStatedCalendar.getTime(), true));
                showOrHideClearDateButtons();
            }
        };
        // Initialize touch listener on the date input
        maxDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarToUse = maxStatedCalendar == null ? nowCalendar : maxStatedCalendar;
                new DatePickerDialog(parentActivity, onMaxDateSetListener,
                        calendarToUse.get(Calendar.YEAR), calendarToUse.get(Calendar.MONTH), calendarToUse.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // MIN DATE
        // Set clear button listener
        clearMinDateInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minStatedCalendar = null;
                minDateInput.setText(null);
                showOrHideClearDateButtons();
            }
        });
        // Set listener for setting date
        final DatePickerDialog.OnDateSetListener onMinDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (minStatedCalendar == null) {
                    minStatedCalendar = Calendar.getInstance(Locale.getDefault());
                }
                minStatedCalendar.set(year, month, dayOfMonth);
                minDateInput.setText(TextFormatService.getFormattedStringFromDate(minStatedCalendar.getTime(), true));
                showOrHideClearDateButtons();
            }
        };
        // Initialize touch listener on the date input
        minDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarToUse = minStatedCalendar == null ? nowCalendar : minStatedCalendar;
                new DatePickerDialog(parentActivity, onMinDateSetListener,
                        calendarToUse.get(Calendar.YEAR), calendarToUse.get(Calendar.MONTH), calendarToUse.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        /**
         * Category input
         */
//        categoriesInput.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start new activity to pick category
//                Intent intent = new Intent(parentActivity, PickCategoryActivity.class);
//                intent.putExtra(SELECTED_CATEGORY_INTENT_NAME, null == statedCategory ? null : statedCategory.getName());
//                startActivityForResult(intent, PICK_CATEGORY);
//            }
//        });

    }

    /**
     * This either shows or hides the clear date button
     */
    private void showOrHideClearDateButtons() {

        // Max Stated Calendar
        if (maxStatedCalendar == null) {
            clearMaxDateInputButton.setVisibility(View.GONE);
        } else {
            clearMaxDateInputButton.setVisibility(View.VISIBLE);
        }

        // Min Stated Calendar
        if (minStatedCalendar == null) {
            clearMinDateInputButton.setVisibility(View.GONE);
        } else {
            clearMinDateInputButton.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Options menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_reset_menu, menu);

        // Set up reset listener
        MenuItem resetItem = menu.findItem(R.id.action_search_reset);
        resetItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // TODO clear all fields
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

}
