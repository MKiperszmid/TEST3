package com.example.com.entregable.View.Fragments;



import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import com.example.com.entregable.Controller.PaintTask;
import com.example.com.entregable.Controller.ResultListener;
import com.example.com.entregable.Model.DAO.AppDatabase;
import com.example.com.entregable.Model.POJO.Artist;
import com.example.com.entregable.Model.POJO.Paint;
import com.example.com.entregable.Model.POJO.PaintContainer;
import com.example.com.entregable.R;
import com.example.com.entregable.Controller.PaintController;
import com.example.com.entregable.Util.Functionality;
import com.example.com.entregable.View.Activities.ExhibicionActivity;
import com.example.com.entregable.View.Adapters.AdapterRecyclerPinturas;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExhibitionFragment extends Fragment implements AdapterRecyclerPinturas.NotificadorCelda{

    private RecyclerView recycler;
    private List<Artist> artistList;
    private NotificadorExhibitionActivity notificadorExhibitionActivity;
    public static final String NOMBRE_SECCION = "Pinturas";
    private ProgressBar progressBar;

    public ExhibitionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.notificadorExhibitionActivity = (NotificadorExhibitionActivity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exhibition, container, false);

        //grabInfo(view);
        //getPaints(view);
        PaintTask paintTask = new PaintTask(view, progressBar, recycler, this, (ExhibicionActivity)getActivity());
        paintTask.execute();
        return view;
    }

    private void grabInfo(final View view){
        Functionality.loadProgressbar(true, progressBar);
        PaintController controller = new PaintController();

        controller.getPaints(new ResultListener<PaintContainer>() {
            @Override
            public void finish(PaintContainer result) {
                AppDatabase appDatabase = AppDatabase.getInstance(view.getContext());
                appDatabase.paintDao().insertAllPaints(result.getPaints());
                loadRecycler(result.getPaints());
            }
        });

        ((ExhibicionActivity)getActivity()).getSupportActionBar().setTitle(NOMBRE_SECCION);
    }

    private void loadRecycler(List<Paint> paints){
        AdapterRecyclerPinturas adapterRecyclerPinturas = new AdapterRecyclerPinturas(paints, ExhibitionFragment.this);
        recycler.setAdapter(adapterRecyclerPinturas);
        recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //recycler.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        Functionality.loadProgressbar(false, progressBar);
    }

    private void getPaints(View view){
        AppDatabase appDatabase = AppDatabase.getInstance(view.getContext());
        List<Paint> container = null;

        container = appDatabase.paintDao().getAllPaints();
        if(container == null || container.size() <= 0){
            grabInfo(view);
        }
        else {
            loadRecycler(container);
        }
    }

    @Override
    public void notificarPintura(Paint paint) {
        notificadorExhibitionActivity.notificar(paint);
    }

    public interface NotificadorExhibitionActivity{
        void notificar(Paint paint);
    }
}
