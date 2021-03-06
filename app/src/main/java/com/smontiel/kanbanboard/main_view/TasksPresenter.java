package com.smontiel.kanbanboard.main_view;

import android.support.annotation.NonNull;
import android.util.Log;

import com.smontiel.kanbanboard.data.DataSource;
import com.smontiel.kanbanboard.data.Task;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Salvador Montiel on 14/11/17.
 */
class TasksPresenter implements TasksContract.Presenter {
    @NonNull
    private final TasksContract.View columnsView;
    @NonNull
    private final DataSource dataSource;
    @NonNull
    private CompositeDisposable compositeDisposable;

    public TasksPresenter(@NonNull TasksContract.View columnsView, @NonNull DataSource dataSource) {
        //TODO: Check not null of parameters
        this.columnsView = columnsView;
        this.dataSource = dataSource;

        compositeDisposable = new CompositeDisposable();
        this.columnsView.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

    @Override
    public void loadTasks(long idColumn) {
        columnsView.setLoadingIndicator(true);

        compositeDisposable.clear();
        Disposable disposable = dataSource.getTasksFromColumn(idColumn)
            .map(new Function<Task, TaskItem>() {
                @Override
                public TaskItem apply(Task task) throws Exception {
                    return new TaskItem(task);
                }
            }).toList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<List<TaskItem>>() {
                @Override
                public void accept(List<TaskItem> taskItems) throws Exception {
                    columnsView.setLoadingIndicator(false);
                    columnsView.showTasks(taskItems);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Log.e("aA", throwable + " : getTasksFromColumn()");
                }
            });
        compositeDisposable.add(disposable);
    }
}
