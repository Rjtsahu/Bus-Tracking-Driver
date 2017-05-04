package com.sahurjt.bstdriver;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sahurjt.bstdriver.Adapter.MyKidAdapter;
import com.sahurjt.bstdriver.Interfaces.ApiInterface;
import com.sahurjt.bstdriver.Models.AttendanceRequest;
import com.sahurjt.bstdriver.Models.Kid;
import com.sahurjt.bstdriver.Models.KidsResponse;
import com.sahurjt.bstdriver.Network.ApiClient;
import com.sahurjt.bstdriver.Service.LocationService;
import com.sahurjt.bstdriver.Utils.ImageDialog;
import com.sahurjt.bstdriver.Utils.SharedPrefHelper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rajat_Sahu on 28-03-2017.
 */
// fragment to show list of kids and mark attendance
public class AtendDialogFrag extends DialogFragment implements View.OnClickListener {

    private RecyclerView rv1; //not attended
    private RecyclerView rv2; // attended
    private MyKidAdapter kidAdapter1;
    private MyKidAdapter kidAdapter2;
    private Button btnOk;
    private Button btnCancel;
    private ApiInterface apiService;

    private static String REQUEST_TYPE;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag__dialog_attend, container, false);
        rv1 = (RecyclerView) rootView.findViewById(R.id.listKid);
        rv2 = (RecyclerView) rootView.findViewById(R.id.listKidAttended);
        btnOk = (Button) rootView.findViewById(R.id.btnOk);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        // create api client
        apiService = ApiClient.getClient().create(ApiInterface.class);
        //createSomeKid();
        rv1.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        rv2.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        // set listener for button
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        this.getDialog().setTitle("Mark Kid Attendance");
        this.getDialog().setCanceledOnTouchOutside(false);
        this.getDialog().setCancelable(false);

        setRequestType();
        setList();

        return rootView;
    }

    private void setRequestType() {
        REQUEST_TYPE = getTag();
    }

    private void setList(){
    // Pick list
        if(REQUEST_TYPE.equals(ApiInterface.QRY_PICKED)){
            listPick();
            listPickAttended();
        }else if (REQUEST_TYPE.equals(ApiInterface.QRY_DROPPED)){
            listDrop();
            listDropAttended();
        }else {
            // incorrect request set
            Log.d("bts_error","incorrect request string");
        }
    }

    // Pick and not yet attended
    private void listPick() {
        apiService.getKids(getToken(), ApiInterface.QRY_KID_NOT_YET_PRESENT).enqueue(new Callback<KidsResponse>() {
            @Override
            public void onResponse(Call<KidsResponse> call, Response<KidsResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(ApiInterface.STATUS_OK)) {
                        kidAdapter1 = new MyKidAdapter(getActivity(),response.body().getKids(),REQUEST_TYPE, ApiInterface.QRY_KID_NOT_YET_PRESENT);
                        rv1.setAdapter(kidAdapter1);
                        setAdapterImageClickListener(kidAdapter1);
                    } else {
                        Log.d("bts_error", "some error in kids response");
                    }
                } else {
                    Log.d("bts_error", "some error in kids response, not 202");
                }
            }

            @Override
            public void onFailure(Call<KidsResponse> call, Throwable t) {
                Log.d("bts_error", "cant load kid list :" + t.getMessage());
                Toast.makeText(getActivity(), "cant get list", Toast.LENGTH_LONG).show();
            }
        });

    }

    //PIck and attended
    private void listPickAttended() {
        apiService.getKids(getToken(), ApiInterface.QRY_KID_PRESENT).enqueue(new Callback<KidsResponse>() {
            @Override
            public void onResponse(Call<KidsResponse> call, Response<KidsResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(ApiInterface.STATUS_OK)) {
                        kidAdapter2 = new MyKidAdapter(getActivity(),response.body().getKids(),REQUEST_TYPE, ApiInterface.QRY_KID_PRESENT);
                        rv2.setAdapter(kidAdapter2);
                        setAdapterImageClickListener(kidAdapter2);
                    } else {
                        Log.d("bts_error", "some error in kids response");
                    }
                } else {
                    Log.d("bts_error", "some error in kids response, not 202");
                }
            }

            @Override
            public void onFailure(Call<KidsResponse> call, Throwable t) {
                Log.d("bts_error", "cant load kid list :" + t.getMessage());
                Toast.makeText(getActivity(), "cant get list", Toast.LENGTH_LONG).show();
            }
        });

    }

    //Drop but not attended
    private void listDrop() {
        apiService.getKidsDropped(getToken(), ApiInterface.QRY_KID_DROP_NOT_YET_PRESENT).enqueue(new Callback<KidsResponse>() {
            @Override
            public void onResponse(Call<KidsResponse> call, Response<KidsResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(ApiInterface.STATUS_OK)) {
                        kidAdapter1 = new MyKidAdapter(getActivity(),response.body().getKids(),REQUEST_TYPE, ApiInterface.QRY_KID_DROP_NOT_YET_PRESENT);
                        rv1.setAdapter(kidAdapter1);
                        setAdapterImageClickListener(kidAdapter1);
                    } else {
                        Log.d("bts_error", "some error in kids response");
                    }
                } else {
                    Log.d("bts_error", "some error in kids response, not 202");
                }
            }

            @Override
            public void onFailure(Call<KidsResponse> call, Throwable t) {
                Log.d("bts_error", "cant load kid list :" + t.getMessage());
                Toast.makeText(getActivity(), "cant get list", Toast.LENGTH_LONG).show();
            }
        });

    }

    //drop and attended
    private void listDropAttended() {
        apiService.getKidsDropped(getToken(), ApiInterface.QRY_KID_DROP_PRESENT).enqueue(new Callback<KidsResponse>() {
            @Override
            public void onResponse(Call<KidsResponse> call, Response<KidsResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(ApiInterface.STATUS_OK)) {
                        kidAdapter2 = new MyKidAdapter(getActivity(),response.body().getKids(),REQUEST_TYPE, ApiInterface.QRY_KID_DROP_PRESENT);
                        rv2.setAdapter(kidAdapter2);
                        setAdapterImageClickListener(kidAdapter2);
                    } else {
                        Log.d("bts_error", "some error in kids response");
                    }
                } else {
                    Log.d("bts_error", "some error in kids response, not 202");
                }
            }

            @Override
            public void onFailure(Call<KidsResponse> call, Throwable t) {
                Log.d("bts_error", "cant load kid list :" + t.getMessage());
                Toast.makeText(getActivity(), "cant get list", Toast.LENGTH_LONG).show();
            }
        });

    }
    // kid image click listener for adapter 1
    private void setAdapterImageClickListener(final MyKidAdapter adapter){
        if (adapter!=null){
        adapter.setKidImageListener(new MyKidAdapter.IKidImageClickListener() {
            @Override
            public void onResult(Bitmap bitmap, String image_url) {
                if (image_url!=null){
                    openImageDialog(image_url);
                }
            }
        });
        }
    }

    private String getToken() {
        return SharedPrefHelper.getInstance(getActivity()).getString(SharedPrefHelper.LOGIN_TOKEN);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnOk) {
            List<Kid> k = kidAdapter1.getItems();

            if (REQUEST_TYPE.equals(ApiInterface.QRY_PICKED)){
                pickAttend(k);
                Log.d("bts_ok","marking pick attendance");
            }else if (REQUEST_TYPE.equals(ApiInterface.QRY_DROPPED)){
                dropAttend(k);
                Log.d("bts_ok","marking drop attendance");
            }
        } else if (view.getId() == R.id.btnCancel) {
            //destroyDial();
            dismiss();
        }
    }

    private List<Integer> getPickupKidIds(List<Kid> kids){
        List<Integer> ids=new ArrayList<Integer>();
        for (Kid k :kids){
            if (k.isChecked()){
                ids.add(k.getId());
            }
        }
        return ids;
    }

    private void pickAttend(List<Kid> kids){
    List<Integer> ids=getPickupKidIds(kids);
        if (ids.isEmpty()){
            Toast.makeText(getActivity(),"No kid selected",Toast.LENGTH_SHORT).show();
        }else {
            apiService.addPickAttends(getToken(),new AttendanceRequest(ids,LocationService.getLatitude(),LocationService.getLongitude())).enqueue(new Callback<com.sahurjt.bstdriver.Models.Response>() {
                @Override
                public void onResponse(Call<com.sahurjt.bstdriver.Models.Response> call, Response<com.sahurjt.bstdriver.Models.Response> response) {
                    if (response.isSuccessful()){
                        if (response.body().getStatus().equals(ApiInterface.STATUS_OK)){
                            Log.d("bts_ok","pick attend success");
                           // Toast.makeText(getActivity(),"OK attendance taken",Toast.LENGTH_SHORT).show();
                        }
                        Log.d("bts_ok","message:"+response.body().getMessage());
                    }

                }

                @Override
                public void onFailure(Call<com.sahurjt.bstdriver.Models.Response> call, Throwable t) {
                    Log.d("bts_error","error :"+t.getMessage());
                }
            });
        }
        dismiss();
    }

    private void dropAttend(List<Kid> kids){
        List<Integer> ids=getPickupKidIds(kids);
        if (ids.isEmpty()){
            Toast.makeText(getActivity(),"No kid selected",Toast.LENGTH_SHORT).show();
        }else {
            apiService.addDropAttends(getToken(),new AttendanceRequest(ids, LocationService.getLatitude(),LocationService.getLongitude())).enqueue(new Callback<com.sahurjt.bstdriver.Models.Response>() {
                @Override
                public void onResponse(Call<com.sahurjt.bstdriver.Models.Response> call, Response<com.sahurjt.bstdriver.Models.Response> response) {
                    if (response.isSuccessful()){
                        if (response.body().getStatus().equals(ApiInterface.STATUS_OK)){
                            Log.d("bts_ok","pick attend success");
                         }
                        Log.d("bts_ok","message:"+response.body().getMessage());
                    }

                }

                @Override
                public void onFailure(Call<com.sahurjt.bstdriver.Models.Response> call, Throwable t) {
                    Log.d("bts_error","error :"+t.getMessage());
                }
            });
        }
        dismiss();
    }

    private void destroyDial(){
        Fragment f=getFragmentManager().findFragmentByTag(REQUEST_TYPE);
        if (f!=null){
            DialogFragment df=(DialogFragment)f;
            df.dismiss();
            getFragmentManager().beginTransaction().remove(f).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("bts_ok","on resume called");
    }

    private byte[] getByteArrayFromBitmap(Bitmap bitmap){
        if (bitmap!=null) {
            // convert bitmap to byte array so it can be converted to bundle
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
             return stream.toByteArray();
        }
        return null;
    }

    private void openImageDialog(String url){
        final ImageDialog dialogFrag=new ImageDialog();
        //dialogFrag.show(fm,FRAG_DROP_TAG);
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        Bundle b = new Bundle();
        b.putString("image_url",url);
        // in case want to send bitmap
        //b.putByteArray("image",getByteArrayFromBitmap(bitmap));
        dialogFrag.setArguments(b);
        dialogFrag.show(ft,url);
    }
}
