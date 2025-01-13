package com.example.personel_takip.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personel_takip.databinding.ItemContainerStaffBinding;
import com.example.personel_takip.listeners.PersonListener;
import com.example.personel_takip.model.Person;

import java.util.List;

    public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder>{
    private List<Person> persons;
    private final PersonListener personListener;

    public PersonAdapter(List<Person> persons, PersonListener personListener){
        this.persons = persons;
        this.personListener = personListener;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerStaffBinding itemContainerStaffBinding = ItemContainerStaffBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new PersonViewHolder(itemContainerStaffBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        holder.setPersonData(persons.get(position));
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    class PersonViewHolder extends RecyclerView.ViewHolder{
        ItemContainerStaffBinding binding;

        public PersonViewHolder(@NonNull ItemContainerStaffBinding itemContainerStaffBinding) {
            super(itemContainerStaffBinding.getRoot());
            binding = itemContainerStaffBinding;
        }
        void setPersonData(Person person){
            binding.textName.setText(person.name);
            binding.textDepartment.setText(person.department);
            binding.imageProfile.setImageBitmap(getPersonImage(person.image));

            binding.getRoot().setOnClickListener(v-> personListener.onPersonClicked(person));
        }
    }
    private Bitmap getPersonImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
