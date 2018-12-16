<!doctype html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="description" content=""/>
    <meta name="keywords" content=""/>
    <title>Android Auto Building</title>

    <style>
        body {
            margin: 0;
            padding: 0;
        }

        #container {
            position: fixed;
        }
    </style>

</head>
<body>

<script charset="GB2312" src="js/three.min.js"></script>

<script charset="GB2312" id="vertexShader" type="x-shader/x-vertex">
    uniform float u_time;

    const float spawnrate = .01;
    const float life = 200.;
    const float fadetime = 20.;
    const int octaves = 5;
    const float seed = 43758.5453123;
    const float seed2 = 73156.8473192;

    float random(float val) {
      return fract(sin(val) * seed);
    }

    vec2 random2(vec2 st, float seed){
        st = vec2( dot(st,vec2(127.1,311.7)),
                  dot(st,vec2(269.5,183.3)) );
        return -1.0 + 2.0*fract(sin(st)*seed);
    }

    float random2d(vec2 uv) {
      return fract(
                sin(
                  dot( uv.xy, vec2(12.9898, 78.233) )
                ) * seed);
    }

  varying float v_z;

  float easeLinear(float time, float begin, float change, float duration)
  {
    return change * time / duration + begin;
  }
  vec2 easeLinear(float time, vec2 begin, vec2 change, float duration)
  {
    return change * time / duration + begin;
  }

  void main() {
    vec4 pos = vec4(position,1.0);
    float id = position.z;
    bool emitter = mod(id, 2.) == 0.;
    float rand = random(id);
    float rand1 = random(id + 1.);
    float pointsize = 100. * rand * rand1;

    // float spawnrate = spawnrate + (sin(u_time / 10.) + 1.) * .01;
    float time = mod(u_time - id * spawnrate, life);
    float step = time / life;
    bool alive = time >= 0.;

    vec2 polar = vec2(0., 0.);

    if(alive) {
      if(emitter) {
        // pos.xy = vec2(10. * rand);
        vec2 outerPolar = vec2(30. + sin(u_time / 50.) * 10., 200.);
        polar = easeLinear(time, vec2(sin(u_time / 50.), 100. + sin(u_time / 10.) * 50.), outerPolar, life);
        // polar.x += sin(u_time / 10. * rand) + 1.;
        polar.y += (sin((u_time + 100.) / 10. * rand) + 1.) * (polar.x * 2. + 10.);

        if(time < fadetime) {
          pointsize = easeLinear(time, 0., pointsize, fadetime);
        } else if(time > life - fadetime) {
          pointsize = easeLinear(time - life + fadetime, pointsize, -pointsize, fadetime);
        }
        pointsize *= (sin((u_time + 100.) / 10. * rand1) + 1.);
        pointsize *= cos(polar.x * 1.5 + u_time * .1) * .5 + 1.;
        pos.z = 100.;
      } else {
        // pos.xy = vec2(10. * rand);
        vec2 outerPolar = vec2(30. + sin(u_time / 50.) * 10. + 3.14, 200.);
        polar = easeLinear(time, vec2(sin(u_time / 50.) + 3.14, 100. + cos(u_time / 10.) * 50.), outerPolar, life);
        // polar.x += sin(u_time / 10. * rand) + 1.;
        polar.y += (sin((u_time + 100.) / 10. * rand) + 1.) * (polar.x * 2. + 10.);

        if(time < fadetime) {
          pointsize = easeLinear(time, 0., pointsize, fadetime);
        } else if(time > life - fadetime) {
          pointsize = easeLinear(time - life + fadetime, pointsize, -pointsize, fadetime);
        }
        pointsize *= 1. - (sin((u_time + 100.) / 10. * rand1) + 1.);
        // pointsize *= cos(polar.x * 1.5 + u_time * .1) * .5 + 1.;
        pos.z = 90.;
      }
    }

    pos.x += cos(polar.x) * polar.y;
    pos.y += sin(polar.x) * polar.y;

    v_z = pos.z / 100. + polar.x / 100.;
    gl_PointSize = pointsize;

    gl_Position = projectionMatrix *
                  modelViewMatrix *
                  pos;
  }



















</script>
<script charset="GB2312" id="fragmentShader" type="x-shader/x-fragment">
    uniform vec2 u_resolution;
    uniform float u_time;
    uniform sampler2D tSprite;

    varying float v_z;

    vec3 hsb2rgb( in vec3 c ){
      vec3 rgb = clamp(abs(mod(c.x*6.0+vec3(0.0,4.0,2.0),
                               6.0)-3.0)-1.0,
                       0.0,
                       1.0 );
      rgb = rgb*rgb*(3.0-2.0*rgb);
      return c.z * mix( vec3(1.0), rgb, c.y);
    }

    #define TAU 6.28318531
    float starSDF(vec2 st, int V, float s) {
      // st = st*4.-2.;
      float a = atan(st.y, st.x)/TAU;
      float seg = a * float(V);
      a = ((floor(seg) + 0.5)/float(V) +
          mix(s,-s,step(.5,fract(seg))))
          * TAU;
      return abs(dot(vec2(cos(a),sin(a)),
                     st));
    }

    void main() {
      vec2 uv = (gl_FragCoord.xy - 0.5 * u_resolution.xy) / u_resolution.y;

      float dist;

      vec2 pointUV = gl_PointCoord.xy - .5 * v_z / v_z;
      dist = 1. - length(pointUV) * 3.;

      vec2 polar = vec2(atan(uv.y, uv.x), length(uv.xy));
      int points = int(dist * 5.);

      // gl_FragColor = vec4(hsb2rgb(vec3(polar.y / 3., 1. - v_z * sin(polar.y * u_time / 100.), polar.y * v_z / 10.)), dist);
      gl_FragColor = vec4(
        mix(
          vec3(5.0, 0., 0.),
          vec3(.8, 1.5, .5),
          clamp(dist * v_z, 0., 1.)
        ) *
        dist,
        smoothstep(0.6, .61 + v_z / 1.5, dist)
      );
      // gl_FragColor *= v_z / 2.;
      // gl_FragColor = vec4(vec3(.8, .8, .5), smoothstep(0.8, .81, dist));
      // gl_FragColor = vec4(v_z / 2.);
      // gl_FragColor = vec4(vec3(dist), dist);
    }




















</script>

<div id="container" dropEffect="link">
</div>

<%--拖拽上传--%>
<script charset="UTF-8" language="javascript" type="text/javascript">
    var defaultSpeed = 0.03;

    function uploadFile(formData) {
        var uploadAjax = new XMLHttpRequest();
        // 分钟超时
        uploadAjax.timeout = 120000;
        uploadAjax.open('POST', '${pageContext.request.contextPath}/createMission', true);
        uploadAjax.send(formData);
        uploadAjax.onreadystatechange = function (ev) {
            if (uploadAjax.readyState === 4 && uploadAjax.status === 200) {
                var mission = JSON.parse(uploadAjax.responseText);
                // 开启轮询
                console.log("create mission:" + mission.missionId);
                queryMission(mission);
            }
        }
    }

    function queryMission(mission) {
        var queryAjax = new XMLHttpRequest();
        queryAjax.timeout = 30000;
        queryAjax.open('GET', '${pageContext.request.contextPath}/queryMission?missionId=' + mission.missionId, true);
        queryAjax.send(null);
        queryAjax.onreadystatechange = function (ev) {
            if (queryAjax.readyState === 4 && queryAjax.status === 200) {
                var missionStatus = queryAjax.responseText;
                console.log("missionId" + mission.missionId + ":status:" + missionStatus);
                if (missionStatus === '0') {
                    // 成功，进行下载文件
                    beginDownLoad(mission.missionId);
                    speed -= defaultSpeed;
                } else if (missionStatus === '1') {
                    // 等待，30秒轮询
                    window.setTimeout(function () {
                        queryMission(mission);
                    }, 30000);
                } else {
                    // 失败，后期加提示
                    alert("Mission Failed:" + mission.zipName + "\nPlease go to the server to view the failed log!");
                    speed -= defaultSpeed;
                }
            }
        }
    }

    function beginDownLoad(missionId) {
        var downLoadAjax = new XMLHttpRequest();
        downLoadAjax.timeout = 120000;
        downLoadAjax.responseType = "blob";
        downLoadAjax.onreadystatechange = function (ev) {
            if (downLoadAjax.readyState === 4 && downLoadAjax.status === 200) {
                try {
                    var response = downLoadAjax.response;
                    var contentHeader = downLoadAjax.getResponseHeader("Content-Disposition");
                    var fileName = contentHeader.split("=")[1];
                    console.log("Begin DownLoad File Name:" + fileName);
                    // 获得fileName
                    if (window.navigator.msSaveOrOpenBlob) {
                        navigator.msSaveBlob(response, fileName);
                    } else {
                        var link = document.createElement('a');
                        link.href = URL.createObjectURL(response);
                        link.download = fileName;
                        link.click();
                        URL.revokeObjectURL(link.href);
                    }
                } catch (e) {
                    alert("DownLoad Error:" + e);
                }
            }
        };
        downLoadAjax.open("GET", '${pageContext.request.contextPath}/downLoadPak?missionId=' + missionId, true);
        downLoadAjax.send();
    }

    var dz = document.getElementById('container');
    dz.ondragover = function (ev) {
        //阻止浏览器默认打开文件的操作
        ev.preventDefault();
        this.className = 'over';
    };

    dz.ondragleave = function () {
        this.className = '';
    };

    dz.ondrop = function (ev) {
        this.className = '';
        //阻止浏览器默认打开文件的操作
        ev.preventDefault();
        //表单上传文件
        var file = ev.dataTransfer.files[0];
        console.log("Begin UpLoad File Name:" + file.name);
        if (file.name.indexOf(".zip") === -1) {
            alert("Upload file must end with .zip, Please use AndroidToZip to compress the file and upload it!");
            return;
        }
        var formData = new FormData();
        formData.append('file', ev.dataTransfer.files[0]);
        speed += defaultSpeed;
        uploadFile(formData);
    }
</script>

<script charset="GB2312" type="text/javascript">
    /*
    Most of the stuff in here is just bootstrapping. Essentially it's just
    setting ThreeJS up so that it renders a flat surface upon which to draw
    the shader. The only thing to see here really is the uniforms sent to
    the shader. Apart from that all of the magic happens in the HTML view
    under the fragment shader.
    */

    var container = void 0;
    var camera = void 0,
        scene = void 0,
        renderer = void 0;
    var uniforms = void 0;

    var speed = 0.02;

    function init() {
        container = document.getElementById('container');

        camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 1, 3000);
        camera.position.z = 600;
        // camera.position.x = -300;
        console.log(camera.lookAt(0, 0, 0));

        scene = new THREE.Scene();

        var geometry = new THREE.Geometry();

        var particleCount = 50000;
        // particleCount = 100;

        for (i = 0; i < particleCount; i++) {

            var vertex = new THREE.Vector3();

            vertex.x = 0;
            vertex.y = 0;
            vertex.z = i;

            geometry.vertices.push(vertex);
        }

        uniforms = {
            u_time: {type: "f", value: -10000.0},
            u_resolution: {type: "v2", value: new THREE.Vector2()},
            u_mouse: {type: "v2", value: new THREE.Vector2()}
        };

        var material = new THREE.ShaderMaterial({
            uniforms: uniforms,
            vertexShader: document.getElementById('vertexShader').textContent,
            fragmentShader: document.getElementById('fragmentShader').textContent
        });
        material.transparent = true;
        material.blending = THREE.AdditiveBlending;
        material.depthTest = false;

        var mesh = new THREE.Points(geometry, material);
        // var mesh = new THREE.Mesh( geometry, starsMaterial );
        scene.add(mesh);

        renderer = new THREE.WebGLRenderer();
        // renderer.setPixelRatio( window.devicePixelRatio );
        renderer.setPixelRatio(1);

        container.appendChild(renderer.domElement);

        onWindowResize();
        window.addEventListener('resize', onWindowResize, false);

        document.onmousemove = function (e) {

            uniforms.u_mouse.value.x = e.pageX;
            uniforms.u_mouse.value.y = e.pageY;
        };
    }

    function onWindowResize(event) {
        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();
        renderer.setSize(window.innerWidth, window.innerHeight);
        uniforms.u_resolution.value.x = renderer.domElement.width;
        uniforms.u_resolution.value.y = renderer.domElement.height;
    }

    function animate() {
        requestAnimationFrame(animate);
        render();
    }

    function render() {
        uniforms.u_time.value += speed;
        renderer.render(scene, camera);
    }

    init();
    animate();
</script>
</body>
</html>