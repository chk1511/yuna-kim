/*
 * FilePondPluginImageTransform 3.0.1
 * Licensed under MIT, https://opensource.org/licenses/MIT
 * Please visit https://pqina.nl/filepond for details.
 */
(function(global, factory) {
  typeof exports === 'object' && typeof module !== 'undefined'
    ? (module.exports = factory())
    : typeof define === 'function' && define.amd
      ? define(factory)
      : (global.FilePondPluginImageTransform = factory());
})(this, function() {
  'use strict';

  // test if file is of type image
  var isImage = function isImage(file) {
    return /^image/.test(file.type);
  };

  var toConsumableArray = function(arr) {
    if (Array.isArray(arr)) {
      for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++)
        arr2[i] = arr[i];

      return arr2;
    } else {
      return Array.from(arr);
    }
  };

  var transforms = {
    1: function _() {
      return [1, 0, 0, 1, 0, 0];
    },
    2: function _(width) {
      return [-1, 0, 0, 1, width, 0];
    },
    3: function _(width, height) {
      return [-1, 0, 0, -1, width, height];
    },
    4: function _(width, height) {
      return [1, 0, 0, -1, 0, height];
    },
    5: function _() {
      return [0, 1, 1, 0, 0, 0];
    },
    6: function _(width, height) {
      return [0, 1, -1, 0, height, 0];
    },
    7: function _(width, height) {
      return [0, -1, -1, 0, height, width];
    },
    8: function _(width) {
      return [0, -1, 1, 0, 0, width];
    }
  };

  var fixImageOrientation = function fixImageOrientation(
    ctx,
    width,
    height,
    orientation
  ) {
    // no orientation supplied
    if (orientation === -1) {
      return;
    }

    ctx.transform.apply(
      ctx,
      toConsumableArray(transforms[orientation](width, height))
    );
  };

  var createVector = function createVector(x, y) {
    return { x: x, y: y };
  };

  var vectorDot = function vectorDot(a, b) {
    return a.x * b.x + a.y * b.y;
  };

  var vectorSubtract = function vectorSubtract(a, b) {
    return createVector(a.x - b.x, a.y - b.y);
  };

  var vectorDistanceSquared = function vectorDistanceSquared(a, b) {
    return vectorDot(vectorSubtract(a, b), vectorSubtract(a, b));
  };

  var vectorDistance = function vectorDistance(a, b) {
    return Math.sqrt(vectorDistanceSquared(a, b));
  };

  var getOffsetPointOnEdge = function getOffsetPointOnEdge(length, rotation) {
    var a = length;

    var A = 1.5707963267948966;
    var B = rotation;
    var C = 1.5707963267948966 - rotation;

    var sinA = Math.sin(A);
    var sinB = Math.sin(B);
    var sinC = Math.sin(C);
    var cosC = Math.cos(C);
    var ratio = a / sinA;
    var b = ratio * sinB;
    var c = ratio * sinC;

    return createVector(cosC * b, cosC * c);
  };

  var getRotatedRectSize = function getRotatedRectSize(rect, rotation) {
    var w = rect.width;
    var h = rect.height;

    var hor = getOffsetPointOnEdge(w, rotation);
    var ver = getOffsetPointOnEdge(h, rotation);

    var tl = createVector(rect.x + Math.abs(hor.x), rect.y - Math.abs(hor.y));

    var tr = createVector(
      rect.x + rect.width + Math.abs(ver.y),
      rect.y + Math.abs(ver.x)
    );

    var bl = createVector(
      rect.x - Math.abs(ver.y),
      rect.y + rect.height - Math.abs(ver.x)
    );

    return {
      width: vectorDistance(tl, tr),
      height: vectorDistance(tl, bl)
    };
  };

  var getImageRectZoomFactor = function getImageRectZoomFactor(
    imageRect,
    cropRect
  ) {
    var rotation =
      arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : 0;
    var center =
      arguments.length > 3 && arguments[3] !== undefined
        ? arguments[3]
        : { x: 0.5, y: 0.5 };

    // calculate available space round image center position
    var cx = center.x > 0.5 ? 1 - center.x : center.x;
    var cy = center.y > 0.5 ? 1 - center.y : center.y;
    var imageWidth = cx * 2 * imageRect.width;
    var imageHeight = cy * 2 * imageRect.height;

    // calculate rotated crop rectangle size
    var rotatedCropSize = getRotatedRectSize(cropRect, rotation);

    // calculate scalar required to fit image
    return Math.max(
      rotatedCropSize.width / imageWidth,
      rotatedCropSize.height / imageHeight
    );
  };

  var getCenteredCropRect = function getCenteredCropRect(
    container,
    aspectRatio
  ) {
    var width = container.width;
    var height = width * aspectRatio;
    if (height > container.height) {
      height = container.height;
      width = height / aspectRatio;
    }
    var x = (container.width - width) * 0.5;
    var y = (container.height - height) * 0.5;

    return {
      x: x,
      y: y,
      width: width,
      height: height
    };
  };

  var calculateCanvasSize = function calculateCanvasSize(
    image,
    canvasAspectRatio
  ) {
    var zoom =
      arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : 1;

    var imageAspectRatio = image.height / image.width;

    // determine actual pixels on x and y axis
    var canvasWidth = 1;
    var canvasHeight = canvasAspectRatio;
    var imgWidth = 1;
    var imgHeight = imageAspectRatio;
    if (imgHeight > canvasHeight) {
      imgHeight = canvasHeight;
      imgWidth = imgHeight / imageAspectRatio;
    }

    var scalar = Math.max(canvasWidth / imgWidth, canvasHeight / imgHeight);

    var width = image.width / (zoom * imgWidth * scalar);
    var height = width * canvasAspectRatio;

    return {
      width: Math.round(width),
      height: Math.round(height)
    };
  };

  var isFlipped = function isFlipped(flip) {
    return flip && (flip.horizontal || flip.vertical);
  };

  var getBitmap = function getBitmap(image, orientation, flip) {
    if (!orientation && !isFlipped(flip)) {
      image.width = image.naturalWidth;
      image.height = image.naturalHeight;
      return image;
    }

    var canvas = document.createElement('canvas');
    var width = image.naturalWidth;
    var height = image.naturalHeight;

    // if is rotated incorrectly swap width and height
    if (orientation >= 5 && orientation <= 8) {
      canvas.width = height;
      canvas.height = width;
    } else {
      canvas.width = width;
      canvas.height = height;
    }

    // draw the image but first fix orientation and set correct flip
    var ctx = canvas.getContext('2d');
    if (orientation) {
      ctx.save();
      fixImageOrientation(ctx, width, height, orientation);
      ctx.restore();
    }

    if (isFlipped(flip)) {
      ctx.translate(canvas.width * 0.5, canvas.height * 0.5);
      ctx.scale(flip.horizontal ? -1 : 1, flip.vertical ? -1 : 1);
      ctx.translate(-canvas.width * 0.5, -canvas.height * 0.5);
    }

    ctx.drawImage(image, 0, 0, width, height);

    return canvas;
  };

  var imageToImageData = function imageToImageData(imageElement, orientation) {
    var crop =
      arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};

    // fixes possible image orientation problems by drawing the image on the correct canvas
    var bitmap = getBitmap(imageElement, orientation, crop.flip);
    var imageSize = {
      width: bitmap.width,
      height: bitmap.height
    };

    var canvas = document.createElement('canvas');
    var aspectRatio = crop.aspectRatio || imageSize.height / imageSize.width;
    var canvasSize = calculateCanvasSize(imageSize, aspectRatio, crop.zoom);
    canvas.width = canvasSize.width;
    canvas.height = canvasSize.height;

    var canvasCenter = {
      x: canvas.width * 0.5,
      y: canvas.height * 0.5
    };

    var imageOffset = {
      x: canvasCenter.x - imageSize.width * (crop.center ? crop.center.x : 0.5),
      y: canvasCenter.y - imageSize.height * (crop.center ? crop.center.y : 0.5)
    };

    var stage = {
      x: 0,
      y: 0,
      width: canvas.width,
      height: canvas.height,
      center: canvasCenter
    };

    var stageZoomFactor = getImageRectZoomFactor(
      imageSize,
      getCenteredCropRect(stage, aspectRatio),
      crop.rotation,
      crop.center
    );

    var scale = (crop.zoom || 1) * stageZoomFactor;

    // start drawing
    var ctx = canvas.getContext('2d');

    // move to draw offset
    ctx.translate(canvasCenter.x, canvasCenter.y);
    ctx.rotate(crop.rotation || 0);
    ctx.scale(scale, scale);

    // draw the image
    ctx.drawImage(
      bitmap,
      imageOffset.x - canvasCenter.x,
      imageOffset.y - canvasCenter.y,
      imageSize.width,
      imageSize.height
    );

    // get data from canvas
    return ctx.getImageData(0, 0, canvas.width, canvas.height);
  };

  var cropSVG = function cropSVG(file, crop) {
    return new Promise(function(resolve, reject) {
      // load file contents and wrap in crop svg
      var fr = new FileReader();
      fr.onloadend = function() {
        // get svg text
        var text = fr.result;

        // create element with svg and get size
        var original = document.createElement('div');
        original.style.cssText =
          'position:absolute;pointer-events:none;width:0;height:0;visibility:hidden;';
        original.innerHTML = text;
        var originalNode = original.querySelector('svg');
        document.body.appendChild(original);

        // request bounding box dimensions
        var bBox = originalNode.getBBox();
        original.parentNode.removeChild(original);

        // get title
        var titleNode = original.querySelector('title');

        // calculate new heights and widths
        var viewBoxAttribute = originalNode.getAttribute('viewBox') || '';
        var widthAttribute = originalNode.getAttribute('width') || '';
        var heightAttribute = originalNode.getAttribute('height') || '';
        var width = parseFloat(widthAttribute) || null;
        var height = parseFloat(heightAttribute) || null;
        var widthUnits = (widthAttribute.match(/[a-z]+/) || [])[0] || '';
        var heightUnits = (heightAttribute.match(/[a-z]+/) || [])[0] || '';

        // create new size
        var viewBoxList = viewBoxAttribute.split(' ').map(parseFloat);
        var viewBox = viewBoxList.length
          ? {
              x: viewBoxList[0],
              y: viewBoxList[1],
              width: viewBoxList[2],
              height: viewBoxList[3]
            }
          : bBox;

        var imageWidth = width != null ? width : viewBox.width;
        var imageHeight = height != null ? height : viewBox.height;

        originalNode.style.overflow = 'visible';
        originalNode.setAttribute('width', imageWidth);
        originalNode.setAttribute('height', imageHeight);

        var aspectRatio = crop.aspectRatio || imageHeight / imageWidth;

        var canvasWidth = imageWidth;
        var canvasHeight = canvasWidth * aspectRatio;

        var canvasZoomFactor = getImageRectZoomFactor(
          {
            width: imageWidth,
            height: imageHeight
          },
          getCenteredCropRect(
            {
              width: canvasWidth,
              height: canvasHeight
            },
            aspectRatio
          ),
          crop.rotation,
          crop.center
        );

        var scale = crop.zoom * canvasZoomFactor;

        var rotation = crop.rotation * (180 / Math.PI);

        var canvasCenter = {
          x: canvasWidth * 0.5,
          y: canvasHeight * 0.5
        };

        var imageOffset = {
          x: canvasCenter.x - imageWidth * crop.center.x,
          y: canvasCenter.y - imageHeight * crop.center.y
        };

        var cropTransforms = [
          // rotate
          'rotate(' +
            rotation +
            ' ' +
            canvasCenter.x +
            ' ' +
            canvasCenter.y +
            ')',

          // scale
          'translate(' + canvasCenter.x + ' ' + canvasCenter.y + ')',
          'scale(' + scale + ')',
          'translate(' + -canvasCenter.x + ' ' + -canvasCenter.y + ')',

          // offset
          'translate(' + imageOffset.x + ' ' + imageOffset.y + ')'
        ];

        var flipTransforms = [
          'scale(' +
            (crop.flip.horizontal ? -1 : 1) +
            ' ' +
            (crop.flip.vertical ? -1 : 1) +
            ')',
          'translate(' +
            (crop.flip.horizontal ? -imageWidth : 0) +
            ' ' +
            (crop.flip.vertical ? -imageHeight : 0) +
            ')'
        ];

        // crop
        var transformed =
          '<?xml version="1.0" encoding="UTF-8"?>\n<svg width="' +
          canvasWidth +
          widthUnits +
          '" height="' +
          canvasHeight +
          heightUnits +
          '" \nviewBox="0 0 ' +
          canvasWidth +
          ' ' +
          canvasHeight +
          '" \npreserveAspectRatio="xMinYMin"\nxmlns="http://www.w3.org/2000/svg">\n<!-- Generator: FilePond Image Transform Plugin - https://pqina.nl/filepond -->\n<title>' +
          (titleNode ? titleNode.textContent : file.name) +
          '</title>\n<desc>Cropped with FilePond.</desc>\n<g transform="' +
          cropTransforms.join(' ') +
          '">\n<g transform="' +
          flipTransforms.join(' ') +
          '">\n' +
          originalNode.outerHTML +
          '\n</g>\n</g>\n</svg>';

        // create new svg file
        resolve(transformed);
      };

      fr.readAsText(file);
    });
  };

  var imageDataToBlob = function imageDataToBlob(imageData, options) {
    return new Promise(function(resolve, reject) {
      var image = document.createElement('canvas');
      image.width = imageData.width;
      image.height = imageData.height;
      var ctx = image.getContext('2d');
      ctx.putImageData(imageData, 0, 0);
      image.toBlob(resolve, options.type, options.quality);
    });
  };

  var objectToImageData = function objectToImageData(obj) {
    var imageData = void 0;
    try {
      imageData = new ImageData(obj.width, obj.height);
    } catch (e) {
      // IE + Old EDGE (tested on 12)
      var canvas = document.createElement('canvas');
      imageData = canvas
        .getContext('2d')
        .createImageData(obj.width, obj.height);
    }
    imageData.data.set(obj.data);
    return imageData;
  };

  var TransformWorker = function TransformWorker() {
    // maps transform types to transform functions
    var transformMatrix = {
      resize: resize
    };

    // applies all image transforms to the image data array
    var applyTransforms = function applyTransforms(transforms, imageData) {
      transforms.forEach(function(transform) {
        imageData = transformMatrix[transform.type](imageData, transform.data);
      });
      return imageData;
    };

    // transform image hub
    var transform = function transform(data, cb) {
      // transform image data
      var imageData = applyTransforms(data.transforms, data.imageData);

      // done
      cb(imageData);
    };

    // route messages
    self.onmessage = function(e) {
      transform(e.data.message, function(response) {
        self.postMessage({ id: e.data.id, message: response }, [
          response.data.buffer
        ]);
      });
    };

    //
    // Transforms
    //
    function resize(imageData, data) {
      var mode = data.mode,
        upscale = data.upscale;
      var _data$size = data.size,
        width = _data$size.width,
        height = _data$size.height;

      if (width === null) {
        width = height;
      } else if (height === null) {
        height = width;
      }

      if (mode !== 'force') {
        var scalarWidth = width / imageData.width;
        var scalarHeight = height / imageData.height;
        var scalar = 1;
        if (mode === 'cover') {
          scalar = Math.max(scalarWidth, scalarHeight);
        } else if (mode === 'contain') {
          scalar = Math.min(scalarWidth, scalarHeight);
        }

        // if image is too small, exit here with original image
        if (scalar > 1 && upscale === false) {
          return imageData;
        }

        width = imageData.width * scalar;
        height = imageData.height * scalar;
      }

      var W = imageData.width;
      var H = imageData.height;
      var W2 = Math.round(width);
      var H2 = Math.round(height);
      var inputData = imageData.data;
      var outputData = new Uint8ClampedArray(W2 * H2 * 4);
      var ratio_w = W / W2;
      var ratio_h = H / H2;
      var ratio_w_half = Math.ceil(ratio_w / 2);
      var ratio_h_half = Math.ceil(ratio_h / 2);

      for (var j = 0; j < H2; j++) {
        for (var i = 0; i < W2; i++) {
          var x2 = (i + j * W2) * 4;
          var weight = 0;
          var weights = 0;
          var weights_alpha = 0;
          var gx_r = (gx_g = gx_b = gx_a = 0);
          var center_y = (j + 0.5) * ratio_h;
          for (var yy = Math.floor(j * ratio_h); yy < (j + 1) * ratio_h; yy++) {
            var dy = Math.abs(center_y - (yy + 0.5)) / ratio_h_half;
            var center_x = (i + 0.5) * ratio_w;
            var w0 = dy * dy; //pre-calc part of w
            for (
              var xx = Math.floor(i * ratio_w);
              xx < (i + 1) * ratio_w;
              xx++
            ) {
              var dx = Math.abs(center_x - (xx + 0.5)) / ratio_w_half;
              var w = Math.sqrt(w0 + dx * dx);
              if (w >= -1 && w <= 1) {
                //hermite filter
                weight = 2 * w * w * w - 3 * w * w + 1;
                if (weight > 0) {
                  dx = 4 * (xx + yy * W);
                  //alpha
                  gx_a += weight * inputData[dx + 3];
                  weights_alpha += weight;
                  //colors
                  if (inputData[dx + 3] < 255)
                    weight = weight * inputData[dx + 3] / 250;
                  gx_r += weight * inputData[dx];
                  gx_g += weight * inputData[dx + 1];
                  gx_b += weight * inputData[dx + 2];
                  weights += weight;
                }
              }
            }
          }
          outputData[x2] = gx_r / weights;
          outputData[x2 + 1] = gx_g / weights;
          outputData[x2 + 2] = gx_b / weights;
          outputData[x2 + 3] = gx_a / weights_alpha;
        }
      }

      return {
        data: outputData,
        width: W2,
        height: H2
      };
    }
  };

  /**
   * Polyfill Edge and IE
   */
  if (!HTMLCanvasElement.prototype.toBlob) {
    Object.defineProperty(HTMLCanvasElement.prototype, 'toBlob', {
      value: function value(cb, type, quality) {
        var canvas = this;
        setTimeout(function() {
          var dataURL = canvas.toDataURL(type, quality).split(',')[1];
          var binStr = atob(dataURL);
          var index = binStr.length;
          var data = new Uint8Array(index);
          while (index--) {
            data[index] = binStr.charCodeAt(index);
          }
          cb(new Blob([data], { type: type || 'image/png' }));
        });
      }
    });
  }

  /**
   * Image Transform Plugin
   */
  var plugin$1 = function(_) {
    var addFilter = _.addFilter,
      utils = _.utils;
    var Type = utils.Type,
      forin = utils.forin,
      loadImage = utils.loadImage,
      getFileFromBlob = utils.getFileFromBlob,
      getFilenameWithoutExtension = utils.getFilenameWithoutExtension,
      createWorker = utils.createWorker,
      createBlob = utils.createBlob,
      renameFile = utils.renameFile,
      isFile = utils.isFile;

    // renames the output file to match the format

    var renameFileToMatchMimeType = function renameFileToMatchMimeType(
      filename,
      format
    ) {
      var name = getFilenameWithoutExtension(filename);
      var extension = format === 'image/jpeg' ? 'jpg' : format.split('/')[1];
      return name + '.' + extension;
    };

    // returns all the valid output formats we can encode towards
    var getOutputMimeType = function getOutputMimeType(type) {
      // allowed formats
      if (type === 'image/jpeg' || type === 'image/png') {
        return type;
      }
      // fallback, will also fix image/jpg
      return 'image/jpeg';
    };

    // valid transforms
    var transformOrder = ['resize'];

    // subscribe to file transformations
    addFilter('PREPARE_OUTPUT', function(file, _ref) {
      var query = _ref.query,
        item = _ref.item;
      return new Promise(function(resolve, reject) {
        // if the file is not an image we do not have any business transforming it
        if (
          !isFile(file) ||
          !isImage(file) ||
          !query('GET_ALLOW_IMAGE_TRANSFORM')
        ) {
          return resolve(file);
        }

        // compression quality 0 => 100
        var qualityAsPercentage = query('GET_IMAGE_TRANSFORM_OUTPUT_QUALITY');
        var quality =
          qualityAsPercentage === null ? null : qualityAsPercentage / 100;
        var qualityMode = query('GET_IMAGE_TRANSFORM_OUTPUT_QUALITY_MODE');

        // output format
        var type = query('GET_IMAGE_TRANSFORM_OUTPUT_MIME_TYPE');

        // get crop

        var _item$getMetadata = item.getMetadata(),
          crop = _item$getMetadata.crop;

        // get transforms

        var transforms = [];
        forin(item.getMetadata(), function(key, value) {
          if (!transformOrder.includes(key)) {
            return;
          }
          transforms.push({
            type: key,
            data: value
          });
        });

        // no transforms defined, or quality change not required, we done!
        if (
          (quality === null ||
            (quality !== null && qualityMode === 'optional')) &&
          type === null &&
          !crop &&
          !transforms.length
        ) {
          return resolve(file);
        }

        // done
        var toBlob = function toBlob(imageData, options) {
          imageDataToBlob(imageData, options)
            .then(function(blob) {
              // transform to file
              var transformedFile = getFileFromBlob(
                blob,
                renameFileToMatchMimeType(
                  file.name,
                  getOutputMimeType(blob.type)
                )
              );

              // we done!
              resolve(transformedFile);
            })
            .catch(function(error) {
              console.error(error);
            });
        };

        // get file url
        var url = URL.createObjectURL(file);

        // if this is an svg and we want it to stay an svg
        if (/svg/.test(file.type) && type === null) {
          // no cropping? Done (as the SVG is vector data we're not resizing it)
          if (!crop) {
            return resolve(file);
          }

          cropSVG(file, crop).then(function(text) {
            resolve(renameFile(createBlob(text, 'image/svg+xml'), file.name));
          });

          return;
        }

        // turn the file into an image
        loadImage(url).then(function(image) {
          // url is no longer needed
          URL.revokeObjectURL(url);

          // get exif orientation
          var orientation = (item.getMetadata('exif') || {}).orientation || -1;

          // draw to canvas and start transform chain
          var imageData = imageToImageData(image, orientation, crop);

          // no further transforms, we done!
          if (!transforms.length) {
            toBlob(imageData, {
              quality: quality,
              type: type || file.type
            });
            return;
          }

          // send to the transform worker
          var worker = createWorker(TransformWorker);
          worker.post(
            {
              transforms: transforms,
              imageData: imageData
            },
            function(response) {
              // finish up
              toBlob(objectToImageData(response), {
                quality: quality,
                type: type || file.type
              });

              // stop worker
              worker.terminate();
            },
            [imageData.data.buffer]
          );
        });
      });
    });

    // Expose plugin options
    return {
      options: {
        allowImageTransform: [true, Type.BOOLEAN],

        // null, 'image/jpeg', 'image/png'
        imageTransformOutputMimeType: [null, Type.STRING],

        // null, 0 - 100
        imageTransformOutputQuality: [null, Type.INT],

        // only apply output quality when a transform is required
        imageTransformOutputQualityMode: ['always', Type.STRING]

        // 'always'
        // 'optional'
        // 'mismatch' (future feature, only applied if quality differs from input)
      }
    };
  };

  var isBrowser =
    typeof window !== 'undefined' && typeof window.document !== 'undefined';

  if (isBrowser && document) {
    document.dispatchEvent(
      new CustomEvent('FilePond:pluginloaded', { detail: plugin$1 })
    );
  }

  return plugin$1;
});
