package com.protovate.verity.data.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Yan on 7/19/15.
 */
public class Jobs implements Serializable {
    public boolean success;
    public Data data;

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

    public class Data implements Serializable {
        public Item items[];
        @SerializedName("_links") public Self links;
        @SerializedName("_meta") public Meta meta;

        public Self getLinks() {
            return links;
        }

        public void setLinks(Self links) {
            this.links = links;
        }

        public Meta getMeta() {
            return meta;
        }

        public void setMeta(Meta meta) {
            this.meta = meta;
        }

        public class Meta {
            public int totalCount;
            public int pageCount;
            public int currentPage;
            public int perPage;

            public int getTotalCount() {
                return totalCount;
            }

            public void setTotalCount(int totalCount) {
                this.totalCount = totalCount;
            }

            public int getPageCount() {
                return pageCount;
            }

            public void setPageCount(int pageCount) {
                this.pageCount = pageCount;
            }

            public int getCurrentPage() {
                return currentPage;
            }

            public void setCurrentPage(int currentPage) {
                this.currentPage = currentPage;
            }

            public int getPerPage() {
                return perPage;
            }

            public void setPerPage(int perPage) {
                this.perPage = perPage;
            }
        }


        public class Self {
            public String href;

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
            }
        }

        public Item[] getItems() {
            return items;
        }

        public void setItems(Item[] items) {
            this.items = items;
        }

        public class Item implements Serializable {
            @SerializedName("case_number") public String caseNumber;
            @SerializedName("file_voice") public String fileVoice;
            @SerializedName("file_picture") public FilePicture filePicture;
            @SerializedName("address_line_1") public String addressLine1;
            @SerializedName("address_line_2") public String addressLine2;
            @SerializedName("audio_count") public int audioCount;
            @SerializedName("video_count") public int videoCount;
            @SerializedName("photo_count") public int photoCount;
            @SerializedName("note_count") public int noteCount;
            @SerializedName("unlock_at") public String unlockAt;
            @SerializedName("created_at") public String createdAt;

            public int id;
            public String city;
            public String state;
            public String country;
            public String zip;
            public String lat;
            public String lng;
            public String notes;
            public String status;
            public Provider provider;

            public String getUnlockAt() {
                return unlockAt;
            }

            public void setUnlockAt(String unlockAt) {
                this.unlockAt = unlockAt;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public String getCaseNumber() {
                return caseNumber;
            }

            public void setCaseNumber(String caseNumber) {
                this.caseNumber = caseNumber;
            }

            public String getFileVoice() {
                return fileVoice;
            }

            public void setFileVoice(String fileVoice) {
                this.fileVoice = fileVoice;
            }

            public FilePicture getFilePicture() {
                return filePicture;
            }

            public void setFilePicture(FilePicture filePicture) {
                this.filePicture = filePicture;
            }

            public String getAddressLine1() {
                return addressLine1;
            }

            public void setAddressLine1(String addressLine1) {
                this.addressLine1 = addressLine1;
            }

            public String getAddressLine2() {
                return addressLine2;
            }

            public void setAddressLine2(String addressLine2) {
                this.addressLine2 = addressLine2;
            }

            public int getAudioCount() {
                return audioCount;
            }

            public void setAudioCount(int audioCount) {
                this.audioCount = audioCount;
            }

            public int getVideoCount() {
                return videoCount;
            }

            public void setVideoCount(int videoCount) {
                this.videoCount = videoCount;
            }

            public int getPhotoCount() {
                return photoCount;
            }

            public void setPhotoCount(int photoCount) {
                this.photoCount = photoCount;
            }

            public int getNoteCount() {
                return noteCount;
            }

            public void setNoteCount(int noteCount) {
                this.noteCount = noteCount;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getZip() {
                return zip;
            }

            public void setZip(String zip) {
                this.zip = zip;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLng() {
                return lng;
            }

            public void setLng(String lng) {
                this.lng = lng;
            }

            public String getNotes() {
                return notes;
            }

            public void setNotes(String notes) {
                this.notes = notes;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public Provider getProvider() {
                return provider;
            }

            public void setProvider(Provider provider) {
                this.provider = provider;
            }

            public class Provider implements Serializable {
                @SerializedName("first_name") public String firstName;
                @SerializedName("last_name") public String lastName;
                @SerializedName("company_name") public String companyName;

                public int id;
                public String email;
                public FilePicture photo;

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

                public FilePicture getPhoto() {
                    return photo;
                }

                public void setPhoto(FilePicture photo) {
                    this.photo = photo;
                }
            }

            public class FilePicture implements Serializable {
                public String orig;
                public String thumb;

                public String getOrig() {
                    return orig;
                }

                public void setOrig(String orig) {
                    this.orig = orig;
                }

                public String getThumb() {
                    return thumb;
                }

                public void setThumb(String thumb) {
                    this.thumb = thumb;
                }
            }
        }
    }
}
