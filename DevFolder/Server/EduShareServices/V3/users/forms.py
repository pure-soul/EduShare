from django import forms
from django.shortcuts import redirect


class UploadForm(forms.Form):
    title = forms.CharField(label="Title", max_length=300)
    authors = forms.CharField(label="Authors", max_length=200)
    publishers = forms.CharField(label="Publishers", max_length=200)
    datePublished = forms.DateField(label="Date Published")
    tags = forms.CharField(label="Tags", max_length=200)

    def full_clean(self):
        if not self.has_changed():
            self._errors = self.ErrorDict()
            return

        return super(UploadForm, self).full_clean()

    def ErrorDict(self):
        return redirect('/v2.1/users/tasks?key=neVEraSkeDaNIgGaFOsh!T,ThATiSSAfetOsAy!&action=upload&process=Please Fill In All Fields (Enter None if applicable)')