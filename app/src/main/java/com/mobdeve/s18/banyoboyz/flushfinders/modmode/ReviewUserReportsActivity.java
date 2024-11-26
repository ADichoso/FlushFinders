package com.mobdeve.s18.banyoboyz.flushfinders.modmode;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.RestroomReviewReportAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.models.RestroomReviewReportData;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ReviewUserReportsActivity extends AppCompatActivity
{

    private RecyclerView rv_restroom_user_reports;
    private ArrayList<RestroomReviewReportData> restroomReviewReportData;
    private RestroomReviewReportAdapter restroomReviewReportAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_user_reports);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv_restroom_user_reports = findViewById(R.id.rv_restroom_user_reports);
        rv_restroom_user_reports.setHasFixedSize(true);
        rv_restroom_user_reports.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        restroomReviewReportData = new ArrayList<RestroomReviewReportData>();

        FirestoreHelper.getInstance().getReviewsDBRef().get()
        .addOnCompleteListener(task -> {
            if(!task.isSuccessful())
                return;

            QuerySnapshot review_documents = task.getResult();

            if(review_documents == null || review_documents.isEmpty())
                return;

            AtomicInteger obtained_reviews = new AtomicInteger();
            for(DocumentSnapshot review_document : review_documents)
            {
                //2. Get the associated restroom
                String restroom_id = review_document.getString(FirestoreReferences.Reviews.RESTROOM);


                FirestoreHelper.getInstance().readRestroom(restroom_id, task1 ->
                {
                    if(!task1.isSuccessful())
                        return;

                    DocumentSnapshot restroom_document = task1.getResult();

                    if(restroom_document == null || !restroom_document.exists())
                        return;

                    //3. Get the building where the restroom may be found
                    FirestoreHelper.getInstance().getReviewsDBRef().whereArrayContains(FirestoreReferences.Buildings.RESTROOMS, restroom_id).limit(1).get().addOnCompleteListener(task2 ->
                    {
                        if(!task2.isSuccessful())
                            return;

                        DocumentSnapshot building_document = task2.getResult().getDocuments().get(0);

                        if(building_document == null || !building_document.exists())
                            return;

                        //4. Create the review data
                        restroomReviewReportData.add(new RestroomReviewReportData(
                            review_document.getId(),
                            building_document.getString(FirestoreReferences.Buildings.NAME),
                            building_document.getString(FirestoreReferences.Buildings.ADDRESS),
                            building_document.getString(FirestoreReferences.Buildings.BUILDING_PICTURE),
                            restroom_id,
                            review_document.getString(FirestoreReferences.Reviews.REVIEWER),
                            review_document.getLong(FirestoreReferences.Reviews.RATING),
                            review_document.getString(FirestoreReferences.Reviews.REPORT),
                            review_document.get(FirestoreReferences.Reviews.CLEANLINESS, Integer.class),
                            review_document.get(FirestoreReferences.Reviews.MAINTENANCE, Integer.class),
                            review_document.get(FirestoreReferences.Reviews.VACANCY, Integer.class))
                        );
                        obtained_reviews.getAndIncrement();
                    });
                });
            }

            if(obtained_reviews.get() == restroomReviewReportData.size())
            {
                restroomReviewReportAdapter = new RestroomReviewReportAdapter(restroomReviewReportData, this);
                rv_restroom_user_reports.setAdapter(restroomReviewReportAdapter);
            }
        });
    }
}