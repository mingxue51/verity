package com.protovate.verity.data.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yan on 7/30/15.
 */
public class Invitations {

    public boolean success;
    public Data data;
    public Info.Error errors[];

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Info.Error[] getErrors() {
        return errors;
    }

    public void setErrors(Info.Error[] errors) {
        this.errors = errors;
    }

    public class Data {
        public Item items[];

        public Item[] getItems() {
            return items;
        }

        public void setItems(Item[] items) {
            this.items = items;
        }

        public class Item {

            public String text = "test";

            public Item() {

            }

            public Item(String text) {
                this.text = text;
            }

            public int id;
            @SerializedName("status_id") public int statusId;
            @SerializedName("status_label") public String statusLabel;
            @SerializedName("created_at") public String createdAt;
            @SerializedName("updated_at") public String updatedAt;
            public Inviter inviter;

            public String getUpdatedAt() {
                return updatedAt;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getStatusId() {
                return statusId;
            }

            public void setStatusId(int statusId) {
                this.statusId = statusId;
            }

            public String getStatusLabel() {
                return statusLabel;
            }

            public void setStatusLabel(String statusLabel) {
                this.statusLabel = statusLabel;
            }

            public Inviter getInviter() {
                return inviter;
            }

            public void setInviter(Inviter inviter) {
                this.inviter = inviter;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public class Inviter {
                public int id;
                @SerializedName("first_name") public String firstName;
                @SerializedName("last_name") public String lastName;
                public String email;
                @SerializedName("company_name") public String companyName;

                public User.Data.Photo photo;
                @SerializedName("created_at") public String createdAt;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getFirstName() {
                    return firstName;
                }

                public void setFirstName(String firstName) {
                    this.firstName = firstName;
                }

                public String getLastName() {
                    return lastName;
                }

                public void setLastName(String lastName) {
                    this.lastName = lastName;
                }

                public String getEmail() {
                    return email;
                }

                public void setEmail(String email) {
                    this.email = email;
                }

                public String getCompanyName() {
                    return companyName;
                }

                public void setCompanyName(String companyName) {
                    this.companyName = companyName;
                }

                public User.Data.Photo getPhoto() {
                    return photo;
                }

                public void setPhoto(User.Data.Photo photo) {
                    this.photo = photo;
                }

                public String getCreatedAt() {
                    return createdAt;
                }

                public void setCreatedAt(String createdAt) {
                    this.createdAt = createdAt;
                }
            }
        }
    }
}
