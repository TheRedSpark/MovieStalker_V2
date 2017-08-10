package moviestalkercom.red_spark.redsparkdev.moviestalker;

import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import moviestalkercom.red_spark.redsparkdev.moviestalker.data.Constants;
import moviestalkercom.red_spark.redsparkdev.moviestalker.data.ItemData;
import moviestalkercom.red_spark.redsparkdev.moviestalker.fragments.ThumbnailFragment;
import moviestalkercom.red_spark.redsparkdev.moviestalker.fragments.adapters.MainFragmentPagerAdapter;
import moviestalkercom.red_spark.redsparkdev.moviestalker.network.GetMovieDataInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static moviestalkercom.red_spark.redsparkdev.moviestalker.data.Constants.TAB_TYPE.MOVIES;

public class MainActivity extends AppCompatActivity implements ThumbnailFragment.OnFragmentInteractionListener{

    private static final String TAG = MainActivity.class.getSimpleName();


    @BindView(R.id.errorText)TextView mErrorView;
    @BindView(R.id.progressBar)ProgressBar mProgressBar;
    @BindView(R.id.pager) ViewPager mPager;

    MainFragmentPagerAdapter mAdapter;

    private ItemData movieData;
    private ItemData tvSeriesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelp.print(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        mAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        //checking for save instance state
        if(savedInstanceState == null) {

            buildTab(MOVIES);
            buildTab(Constants.TAB_TYPE.SERIES);
            buildTab(Constants.TAB_TYPE.FAVORITES);

        }else{
            movieData = (ItemData) savedInstanceState
                    .getSerializable(Constants.TAB_TYPE.MOVIES.getTag());
            tvSeriesData = (ItemData) savedInstanceState
                    .getSerializable(Constants.TAB_TYPE.SERIES.getTag());

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.TAB_TYPE.MOVIES.getTag(), movieData);
        outState.putSerializable(Constants.TAB_TYPE.SERIES.getTag(), tvSeriesData);
    }

    @Override
    public void onThumbnailClick(Uri uri) {
        // TODO: 09-Aug-17

    }

    @Override
    public void onError() {
       // mFragmentManager.beginTransaction().remove(topMoviesFragment).commit();
       // mErrorView.setVisibility(View.VISIBLE);
       // mProgressBar.setVisibility(View.GONE);

    }

    private void buildTab(final Constants.TAB_TYPE tabType){
        //Creating retrofit builder instance
        final Retrofit.Builder builder =  new Retrofit.Builder()
                //adding a base url that will be quarried
                .baseUrl(Constants.MOVIE_BASE_URL)
                //specifying what converter we will use
                //since we are getting data in json format well use GSON
                .addConverterFactory(GsonConverterFactory.create());

        //Creating the actual retrofit object
        Retrofit retrofit = builder.build();
        //Instance of the retrofit interface
        GetMovieDataInterface networkInterface = retrofit.create(GetMovieDataInterface.class);

        //Calling the action on the interface(we only have one and it's a @GET request)
        //We also specify the variables of the url



        String url = Constants.MOVIE_BASE_URL
                + tabType.getTag()
                + "/top_rated?api_key="
                + getString(R.string.api_key);
        Call<ItemData> networkCall = networkInterface
                .movieList(url);

        mProgressBar.setVisibility(View.VISIBLE);
        //executing the request asynchronously
        networkCall.enqueue(new Callback<ItemData>() {

            @Override
            public void onResponse(Call<ItemData> call, Response<ItemData> response) {

                switch (tabType){
                    case MOVIES:
                        movieData = response.body(); break;
                    case SERIES:
                        tvSeriesData = response.body(); break;
                }

                //Removing the progress bar since we have loaded the data
                mProgressBar.setVisibility(View.GONE);

                //get(0) since there should only be one list item in the response
                if(response.body() != null){


                    ArrayList<String> thumbnails = extractThumbnails(response.body());
                    FragmentManager mFragmentManager = getSupportFragmentManager();
                    List<Fragment> fragments = mFragmentManager.getFragments();
                    ThumbnailFragment movieFragment = (ThumbnailFragment)fragments.get(tabType.getPosition());
                    movieFragment.update(thumbnails);




                    mErrorView.setVisibility(View.GONE);
                }else{
                     mErrorView.setVisibility(View.VISIBLE);
                }



            }

            @Override
            public void onFailure(Call<ItemData> call, Throwable t) {
                mErrorView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }

            private ArrayList<String> extractThumbnails(ItemData data){
                ArrayList<String> thumbnails = new ArrayList<>();
                for(ItemData.Result result: data.results){
                    thumbnails.add(result.getPosterPath());
                }
                return thumbnails;
            }
        });

    }
}
