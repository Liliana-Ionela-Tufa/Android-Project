package com.example.licenta2024;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PickAttraction extends AppCompatActivity {

    private PlacesClient placesClient;
    private AutocompleteSessionToken autocompleteSessionToken;
    private EditText editText;

    private PlacesAdapter placesAdapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_attraction);

        String API_Key = "AIzaSyBu25Kb4hIc6GtTGinKoL_l7URTyJDSNk8";
        if (!Places.isInitialized()){
            Places.initialize(this, API_Key, Locale.ENGLISH);

        }

        placesClient = Places.createClient(this);
        autocompleteSessionToken = AutocompleteSessionToken.newInstance();

        firestore = FirebaseFirestore.getInstance();

        editText = findViewById(R.id.searchAttraction);
        ListView listView = findViewById(R.id.list);

        placesAdapter = new PlacesAdapter(this);
        listView.setAdapter(placesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (placesAdapter.getCount() > 0) {
                    detailPlace(placesAdapter.predictionList.get(i).getPlaceId());
                    Intent intent = new Intent(PickAttraction.this, UploadAttraction.class);
                    intent.putExtra("id", placesAdapter.predictionList.get(i).getPlaceId());
                    startActivity(intent);
                }
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    if (editText.length() > 0)
                        searchPlaces();
                }
                return false;
            }
        });
    }

    private void detailPlace(String placeID) {
        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.PHOTO_METADATAS,
                Place.Field.OPENING_HOURS
        );
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeID, placeFields);
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                if (place.getPhotoMetadatas() != null && !place.getPhotoMetadatas().isEmpty()) {
                    // Fetch the first photo
                    fetchPlacePhoto(place, place.getPhotoMetadatas().get(0));
                } else {
                    Log.d("PickAttraction", "No photo metadata found for place: " + place.getName());
                    savePlaceToFirestore(place, null);
                }
            }
        });
    }

    private void fetchPlacePhoto(Place place, PhotoMetadata photoMetadata) {
        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500)
                .setMaxHeight(500)
                .build();

        placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
            @Override
            public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                uploadPhotoToStorage(place, bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                savePlaceToFirestore(place, null);
            }
        });
    }

    private void uploadPhotoToStorage(Place place, Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("place_photos/" + place.getId() + ".jpg");
        UploadTask uploadTask = storageRef.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        savePlaceToFirestore(place, uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                savePlaceToFirestore(place, null);
            }
        });
    }

    private void savePlaceToFirestore(Place place, String photoURL) {
        if (place.getLatLng() != null) {
            Map<String, Object> placeData = new HashMap<>();
            placeData.put("uuid", place.getId());
            placeData.put("name", place.getName());
            placeData.put("lat", place.getLatLng().latitude);
            placeData.put("lng", place.getLatLng().longitude);
            placeData.put("address", place.getAddress());
            placeData.put("imageURL", photoURL);


            String city = null;
            String country = null;
            for (AddressComponent component : place.getAddressComponents().asList()) {
                for (String type : component.getTypes()) {
                    if (type.equals("locality")) {
                        city = component.getName();
                    } else if (type.equals("country")) {
                        country = component.getName();
                    }
                }
            }

            placeData.put("city", city);
            placeData.put("country", country);
            if (place.getOpeningHours() != null) {
                placeData.put("openinghours", place.getOpeningHours().getPeriods());
            }


            DocumentReference documentReference = firestore.collection("touristic-attraction").document(String.valueOf(place.getId()));
            documentReference.set(placeData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("PickAttraction", "Document successfully written!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("PickAttraction", "Error writing document", e);
                }
            });

//            Intent intent = new Intent(PickAttraction.this, UploadAttraction.class);
//            intent.putExtra("id", place.getId());
//            intent.putExtra("name", place.getName());
//            intent.putExtra("lat", String.valueOf(place.getLatLng().latitude));
//            intent.putExtra("lng", String.valueOf(place.getLatLng().longitude));
//            intent.putExtra("address", place.getAddress());
//            intent.putExtra("photoUrl", photoURL);
//            intent.putExtra("city", city);
//            intent.putExtra("country", country);
//            startActivity(intent);
        }


    }

    private void searchPlaces() {
        final LocationBias bias = RectangularBounds.newInstance(
                new LatLng(44.426765, 26.102537),
                new LatLng(45.641312, 25.640950)
        );

        final FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest
                .builder()
                .setSessionToken(autocompleteSessionToken)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setQuery(editText.getText().toString())
                .setLocationBias(bias)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
                List<AutocompletePrediction> predictionList = findAutocompletePredictionsResponse.getAutocompletePredictions();
                placesAdapter.setPredictionList(predictionList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.e("PickAttraction", "Place not found" + apiException.getStatusCode());
                }
            }
        });
    }

    private static class PlacesAdapter extends BaseAdapter {

        private final List<AutocompletePrediction> predictionList = new ArrayList<>();
        private final android.content.Context context;

        public PlacesAdapter(Context context) {
            this.context = context;
        }

        public void setPredictionList(List<AutocompletePrediction> predictionList) {
            this.predictionList.clear();
            this.predictionList.addAll(predictionList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return predictionList.size();
        }

        @Override
        public Object getItem(int i) {
            return predictionList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_list, viewGroup, false);
            TextView shortText = v.findViewById(R.id.shortDesc);
            TextView longText = v.findViewById(R.id.longDesc);

            shortText.setText(predictionList.get(i).getPrimaryText(null));
            longText.setText(predictionList.get(i).getSecondaryText(null));
            return v;
        }
    }
}
