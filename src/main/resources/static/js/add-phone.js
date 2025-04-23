document.addEventListener('DOMContentLoaded', function() {
    const fileInput = document.querySelector('input[type="file"]');
    const imagePreview = document.getElementById('imagePreview');
    const imagePreviewSection = document.querySelector('.image-preview-section');
    const dragDropArea = document.getElementById('dragDropArea');
    const browseButton = document.querySelector('.browse-button');
    const thumbnailIndexInput = document.getElementById('thumbnailIndex');
    const form = document.querySelector('.phone-add-form');
    let selectedImages = [];
    const MAX_IMAGES = 6;

    form.addEventListener('submit', function(e) {
        if (selectedImages.length === 0) {
            e.preventDefault();
            alert('Please add at least one phone picture before submitting.');
            return false;
        }

        const orderedFiles = Array.from(imagePreview.children).map(wrapper => {
            const index = parseInt(wrapper.dataset.index);
            return selectedImages[index].file;
        });

        const dataTransfer = new DataTransfer();
        orderedFiles.forEach(file => {
            dataTransfer.items.add(file);
        });
        fileInput.files = dataTransfer.files;

        thumbnailIndexInput.value = '0';
    });

    const sortable = new Sortable(imagePreview, {
        animation: 150,
        ghostClass: 'sortable-ghost',
        onEnd: function(evt) {
            updateImageNumbers();
            updateThumbnailBadge();
            updateThumbnailIndex();
            updateSelectedImagesOrder();
        }
    });

    browseButton.addEventListener('click', function() {
        fileInput.click();
    });

    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        dragDropArea.addEventListener(eventName, preventDefaults, false);
    });

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    ['dragenter', 'dragover'].forEach(eventName => {
        dragDropArea.addEventListener(eventName, highlight, false);
    });

    ['dragleave', 'drop'].forEach(eventName => {
        dragDropArea.addEventListener(eventName, unhighlight, false);
    });

    function highlight() {
        dragDropArea.classList.add('highlight');
    }

    function unhighlight() {
        dragDropArea.classList.remove('highlight');
    }

    document.addEventListener('paste', function(e) {
        if (e.clipboardData && e.clipboardData.items) {
            const items = e.clipboardData.items;
            const files = [];
            
            for (let i = 0; i < items.length; i++) {
                if (items[i].type.indexOf('image') !== -1) {
                    const file = items[i].getAsFile();
                    if (file) {
                        files.push(file);
                    }
                }
            }
            
            if (files.length > 0) {
                handleFiles(files);
            }
        }
    });

    dragDropArea.addEventListener('drop', function(e) {
        const items = e.dataTransfer.items;
        const files = [];
        
        if (items) {
            for (let i = 0; i < items.length; i++) {
                if (items[i].kind === 'file') {
                    const file = items[i].getAsFile();
                    if (file && file.type.startsWith('image/')) {
                        files.push(file);
                    }
                }
            }
        }
        
        if (e.dataTransfer.types.includes('text/uri-list')) {
            const url = e.dataTransfer.getData('text/uri-list');
            if (url.match(/\.(jpeg|jpg|png)$/i)) {
                fetch(url)
                    .then(response => response.blob())
                    .then(blob => {
                        const file = new File([blob], url.split('/').pop(), { type: blob.type });
                        handleFiles([file]);
                    })
                    .catch(error => console.error('Error fetching image:', error));
                return;
            }
        }
        
        if (files.length > 0) {
            handleFiles(files);
        }
    });

    fileInput.addEventListener('change', function(e) {
        handleFiles(this.files);
    });

    function handleFiles(files) {
        const fileList = Array.from(files);
        
        if (selectedImages.length + fileList.length > MAX_IMAGES) {
            alert(`You can only upload a maximum of ${MAX_IMAGES} images. You already have ${selectedImages.length} images selected.`);
            fileInput.value = '';
            return;
        }

        const newFiles = fileList.map(newFile => {
            const isDuplicate = selectedImages.some(existingImage =>
                existingImage.file.name === newFile.name
            );
            
            if (isDuplicate) {
                const timestamp = Date.now();
                const fileExtension = newFile.name.split('.').pop();
                const baseName = newFile.name.substring(0, newFile.name.lastIndexOf('.'));
                const uniqueName = `${baseName}_${timestamp}.${fileExtension}`;
                
                return new File([newFile], uniqueName, {
                    type: newFile.type,
                    lastModified: newFile.lastModified
                });
            }
            return newFile;
        });

        if (newFiles.length === 0) {
            fileInput.value = '';
            return;
        }

        imagePreviewSection.style.display = 'block';
        
        newFiles.forEach((file, index) => {
            const reader = new FileReader();
            
            reader.onload = function(e) {
                const wrapper = document.createElement('div');
                wrapper.className = 'preview-image-wrapper';
                wrapper.dataset.index = selectedImages.length;
                
                const img = document.createElement('img');
                img.src = e.target.result;
                img.className = 'preview-image';
                
                const numberBadge = document.createElement('span');
                numberBadge.className = 'image-number';
                numberBadge.textContent = selectedImages.length + 1;
                
                const thumbnailBadge = document.createElement('span');
                thumbnailBadge.className = 'thumbnail-badge';
                thumbnailBadge.textContent = 'Thumbnail';
                thumbnailBadge.style.display = selectedImages.length === 0 ? 'block' : 'none';

                const removeButton = document.createElement('button');
                removeButton.className = 'remove-image-button';
                removeButton.innerHTML = '×';
                removeButton.addEventListener('click', function(e) {
                    e.stopPropagation();
                    removeImage(wrapper);
                });
                
                wrapper.appendChild(img);
                wrapper.appendChild(numberBadge);
                wrapper.appendChild(thumbnailBadge);
                wrapper.appendChild(removeButton);
                
                imagePreview.appendChild(wrapper);
                selectedImages.push({
                    file: file,
                    preview: wrapper,
                    index: selectedImages.length
                });

                if (selectedImages.length === fileList.length) {
                    updateImageNumbers();
                    updateThumbnailBadge();
                    updateThumbnailIndex();
                }
            };
            
            reader.readAsDataURL(file);
        });

        fileInput.value = '';
    }

    function updateSelectedImagesOrder() {
        const currentOrder = Array.from(imagePreview.children).map(wrapper =>
            parseInt(wrapper.dataset.index)
        );
        
        const reorderedImages = currentOrder.map(index => selectedImages[index]);
        selectedImages = reorderedImages;
        
        selectedImages.forEach((image, i) => {
            image.index = i;
            image.preview.dataset.index = i;
        });
    }

    function removeImage(wrapper) {
        const index = parseInt(wrapper.dataset.index);
        wrapper.remove();
        selectedImages.splice(index, 1);
        
        selectedImages.forEach((image, i) => {
            image.index = i;
            image.preview.dataset.index = i;
        });
        
        updateImageNumbers();
        updateThumbnailBadge();
        updateThumbnailIndex();
        
        if (selectedImages.length === 0) {
            imagePreviewSection.style.display = 'none';
        }
    }

    function updateImageNumbers() {
        const wrappers = imagePreview.querySelectorAll('.preview-image-wrapper');
        wrappers.forEach((wrapper, index) => {
            wrapper.querySelector('.image-number').textContent = index + 1;
            wrapper.dataset.index = index;
        });
    }

    function updateThumbnailBadge() {
        const wrappers = imagePreview.querySelectorAll('.preview-image-wrapper');
        wrappers.forEach((wrapper, index) => {
            const badge = wrapper.querySelector('.thumbnail-badge');
            badge.style.display = index === 0 ? 'block' : 'none';
        });
    }

    function updateThumbnailIndex() {
        thumbnailIndexInput.value = '0';
    }
}); 