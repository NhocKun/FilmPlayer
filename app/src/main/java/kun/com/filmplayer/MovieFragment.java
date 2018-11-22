package kun.com.filmplayer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MovieFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_TYPE = "Type";
    // TODO: Customize parameters
    private OnListFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MovieAdapter adapter;
    private MovieType _type;
    private List<MovieInfo> lst;
    private RecyclerView recyclerView;

    public enum MovieType {
        Home(1), Movie(2), SeriesMovie(3), Presents(4), Theater(5);

        private int type;

        MovieType(int i) {
            this.type = i;
        }

        public int getType() {
            return type;
        }

    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MovieFragment newInstance(MovieType type) {
        MovieFragment fragment = new MovieFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type.getType());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            _type = MovieType.valueOf(String.valueOf(getArguments().getInt(ARG_TYPE)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        initSw();
        initData();
        handleAction();
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
            //recyclerView.setAdapter(new MovieAdapter(null, mListener));
        }
        return view;
    }


    final String HOME_URL = "http://www.phimmoi.net/";
    final String MOVIE_URL = "http://www.phimmoi.net/phim-le/";
    final String SERIES_MOVIE_URL = "http://www.phimmoi.net/phim-bo/";
    final String PRESENTS_URL = "http://www.phimmoi.net/phim-thuyet-minh/";
    final String THEATER_URL = "http://www.phimmoi.net/phim-chieu-rap/";


    final String _classMoive = "movie-list-index";
    final String _item = "movie-item";

    private void initData() {
        String _url = getUrlByType();
        try {
            Document doc = Jsoup.connect(_url).get();
            Elements elements = doc.getElementsByClass(_classMoive).get(0).getElementsByClass(_item);
            for (Element e : elements) {
                MovieInfo info = new MovieInfo();

                //Get url
                info.setMovieUrl(e.getElementsByTag("a[href]").get(0).text());
                info.setMovieName(e.getElementsByClass("movie-title-1").get(0).text());
                info.setMovieLength(e.getElementsByClass("movie-title-chap").get(0).text());
                info.setMovieRibbon(e.getElementsByClass("ribbon").get(0).text());
                String thumb = e.getElementsByClass("movie-thumbnail").get(0).getElementsByAttribute("style").text();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getUrlByType() {
        switch (_type) {
            case Home:
                return HOME_URL;
            case Movie:
                return MOVIE_URL;
            case SeriesMovie:
                return SERIES_MOVIE_URL;
            case Theater:
                return THEATER_URL;
            case Presents:
                return PRESENTS_URL;
            default:
                return "";
        }

    }

    private void initSw() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lst.clear();
                initData();
            }
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void handleAction() {
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!swipeRefreshLayout.isRefreshing()) {
                    Intent intent = new Intent(getActivity(), DetailMovie.class);
                    intent.putExtra("info", lst.get(position).toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }));
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(MovieInfo item);
    }
}
